from .printable import *
from .validation import *


LINPROG_SOLVERS = ['apache', 'ojalgo']
KERNELS = ['linear', 'gaussian', 'diagonal']
SAMPLE_SELECTORS = ['single', 'independent']


class Learner(Printable):
    pass


class Kernel(Printable):
    def __init__(self, name='gaussian', gamma=0, diagonal=[]):
        super().__init__(name=name)

        assert_in_list(name, KERNELS)
        assert_positive('gamma', gamma, allow_zero=True)
        if any([d <= 0 for d in diagonal]):
            raise ValueError("Non-negative value found in diagonal.")

        if self.name == 'gaussian':
            self.gamma = gamma

        if self.name == 'diagonal':
            self.diagonal = diagonal

    def __repr__(self):
        return 'linear' if self.name == 'linear' else 'gaussian gamma=' + str(self.gamma)


class SVM(Learner):
    def __init__(self, C=1.0, kernel='gaussian', gamma=0):
        super().__init__()

        assert_positive('C', C)

        self.C = C
        self.kernel = Kernel(kernel, gamma)


class KNN(Learner):
    def __init__(self, k=5, gamma=0.):
        super().__init__()

        assert_positive('k', k)
        assert_in_range('gamma', gamma, lower=0, upper=1)

        self.k = k
        self.gamma = gamma


class SampleSelector(Printable):
    def __init__(self, name="single", warmup=100, thin=10, chain_length=64):
        super().__init__(name='WarmUpAndThin' if name == 'single' else 'IndependentChains')

        assert_positive('warmup', warmup, allow_zero=True)
        assert_positive('thin', thin)
        assert_positive('chain_length', chain_length)
        assert_in_list(name, SAMPLE_SELECTORS)

        if self.name == 'WarmUpAndThin':
            self.warmUp = warmup
            self.thin = thin
        else:
            self.chainLength = chain_length


class HitAndRun(Printable):
    def __init__(self, selector, rounding=True, max_iter=0, cache=True, rounding_cache=False):
        super().__init__(add_name=False)

        self.cache = cache
        self.rounding = rounding
        self.selector = selector
        self.roundingCache = rounding_cache

        if rounding and max_iter > 0:
            self.maxIter = max_iter


class BayesianSampler(Printable):
    def __init__(self, warmup, thin, sigma):
        super().__init__(add_name=False)
        assert_positive("warmup", warmup)
        assert_positive("thin", thin)
        assert_positive("sigma", sigma)

        self.warmup = warmup
        self.thin = thin
        self.sigma = sigma


class VersionSpace(Printable):
    def __init__(self, hit_and_run,
                 kernel='linear', gamma=0, jitter=0, diagonal=(),
                 decompose=False, sphere=False, add_intercept=True, solver="ojalgo"):
        super().__init__(add_name=False)

        assert_in_list(solver, LINPROG_SOLVERS)
        assert_positive("jitter", jitter, allow_zero=True)

        self.addIntercept = add_intercept
        self.solver = solver
        self.kernel = Kernel(kernel, gamma, diagonal)
        self.hitAndRunSampler = hit_and_run
        self.decompose = decompose
        if decompose and jitter > 0:
            self.jitter = jitter
        if sphere:
            self.sphere = sphere


class BayesianVersionSpace(Printable):
    def __init__(self, warmup, thin, sigma, kernel='linear', gamma=0, add_intercept=True):
        super().__init__(add_name=False)

        self.addIntercept = add_intercept
        self.kernel = Kernel(kernel, gamma)
        self.bayesianSampler = BayesianSampler(warmup, thin, sigma)


class MajorityVote(Learner):
    def __init__(self, num_samples=8,
                 warmup=100, thin=10, chain_length=64, selector="single",
                 rounding=True, max_iter=0, cache=False, rounding_cache=False,
                 kernel='linear', gamma=0, jitter=0, diagonal=(),
                 decompose=False, sphere=False, add_intercept=True, solver="ojalgo"):
        super().__init__()

        assert_positive('num_samples', num_samples)
        assert_positive("max_iter", max_iter, True)

        if rounding and rounding_cache and not decompose:
            raise ValueError("Cannot use rounding_cache without decomposition")

        self.sampleSize = num_samples
        self.versionSpace = VersionSpace(
            hit_and_run=HitAndRun(
                selector=SampleSelector(name=selector, warmup=warmup, thin=thin, chain_length=chain_length),
                rounding=rounding, max_iter=max_iter, cache=cache,
                rounding_cache=rounding_cache,
            ),
            kernel=kernel, gamma=gamma, diagonal=diagonal, jitter=jitter,
            decompose=decompose, sphere=sphere, add_intercept=add_intercept, solver=solver
        )


class BayesianMajorityVote(Learner):
    def __init__(self, num_samples=8,
                 warmup=100, thin=10, sigma=1.0,
                 kernel='linear', gamma=0,
                 add_intercept=True):
        super().__init__(name="MajorityVote")

        self.sampleSize = num_samples
        self.versionSpace = BayesianVersionSpace(
            warmup=warmup, thin=thin, sigma=sigma,
            kernel=kernel, gamma=gamma,
            add_intercept=add_intercept
        )


class SubspatialLearner(Learner):
    def __init__(self, learners, threads=0):
        super().__init__()

        assert_positive('threads', threads, allow_zero=True)

        self.subspaceLearners = learners

        if threads > 0:
            self.numThreads = threads

    def set_repeat(self, repeat):
        if isinstance(self.subspaceLearners, Learner):
            self.repeat = repeat

    def set_categorical(self, categorical):
        if categorical:
            self.categorical = categorical
