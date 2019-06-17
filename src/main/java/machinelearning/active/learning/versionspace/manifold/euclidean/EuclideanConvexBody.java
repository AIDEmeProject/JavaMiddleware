package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Manifold;

public interface EuclideanConvexBody extends ConvexBody {
    Manifold manifold = new EuclideanSpace();

    @Override
    default Manifold getManifold() {
        return manifold;
    }

}
