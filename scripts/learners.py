from printable import *
from validation import *


LINPROG_SOLVERS = ['apache', 'ojalgo']
KERNELS = ['linear', 'gaussian']
SAMPLE_SELECTORS = ['single', 'independent']


class Learner(Printable):
    pass


class Kernel(Printable):
    def __init__(self, name='gaussian', gamma=0):
        super().__init__(name=name)

        assert_in_list(name, KERNELS)
        assert_positive('gamma', gamma, allow_zero=True)

        if self.name != 'linear':
            self.gamma = gamma

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
    def __init__(self, selector, rounding=True, cache=True):
        super().__init__(add_name=False)
        self.cache = cache
        self.rounding = rounding
        self.selector = selector


class VersionSpace(Printable):
    def __init__(self, hit_and_run,
                 kernel='linear', gamma=0,
                 add_intercept=True, solver="ojalgo"):
        super().__init__(add_name=False)

        assert_in_list(solver, LINPROG_SOLVERS)

        self.addIntercept = add_intercept
        self.solver = solver
        self.kernel = Kernel(kernel, gamma)
        self.hitAndRunSampler = hit_and_run


class MajorityVote(Learner):
    def __init__(self, num_samples=8,
                 warmup=100, thin=10, chain_length=64, selector="single", rounding=True, cache=True,
                 kernel='linear', gamma=0,
                 add_intercept=True, solver="ojalgo"):
        super().__init__()

        assert_positive('num_samples', num_samples)

        self.sampleSize = num_samples
        self.versionSpace = VersionSpace(
            hit_and_run=HitAndRun(
                selector=SampleSelector(name=selector, warmup=warmup, thin=thin, chain_length=chain_length),
                rounding=rounding, cache=cache
            ),
            kernel=kernel, gamma=gamma,
            add_intercept=add_intercept, solver=solver
        )
