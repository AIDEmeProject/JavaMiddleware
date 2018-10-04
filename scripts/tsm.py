from .printable import Printable
from .validation import assert_in_range


class MultipleTSM(Printable):
    def __init__(self, feature_groups=None, tsm_flags=None, sample_from_unknown_region_probability=0):
        super().__init__(add_name=False)

        assert_in_range("UNCERTAIN_SET_SAMPLE_PROBABILITY", sample_from_unknown_region_probability, 0, 1)

        if (not feature_groups and tsm_flags) or (not tsm_flags and feature_groups):
            raise ValueError("Feature groups and TSM flags must both be specified")

        self.featureGroups = feature_groups
        self.flags = tsm_flags
        self.searchUnknownRegionProbability = sample_from_unknown_region_probability
