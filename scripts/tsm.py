from .printable import Printable
from .validation import assert_in_range, assert_same_lengths


class MultipleTSM(Printable):
    def __init__(self, feature_groups, is_convex_positive, is_categorical, sample_from_unknown_region_probability):
        super().__init__(add_name=False)

        assert_in_range("UNCERTAIN_SET_SAMPLE_PROBABILITY", sample_from_unknown_region_probability, 0, 1)
        assert_same_lengths(feature_groups, is_convex_positive, is_categorical)

        self.featureGroups = feature_groups
        self.flags = list(zip(is_convex_positive, is_categorical))
        self.searchUnknownRegionProbability = sample_from_unknown_region_probability
