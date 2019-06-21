package machinelearning.active.learning.versionspace.manifold.euclidean;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.Manifold;

public interface EuclideanConvexBody extends ConvexBody {
    @Override
    default Manifold getManifold() {
        return EuclideanSpace.getInstance();
    }

}
