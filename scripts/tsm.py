from .printable import Printable
from .validation import assert_in_range


class MultipleTSM(Printable):
    def __init__(self, sample_from_unknown_region_probability):
        super().__init__(add_name=False)

        assert_in_range("UNCERTAIN_SET_SAMPLE_PROBABILITY", sample_from_unknown_region_probability, 0, 1)

        self.searchUnknownRegionProbability = sample_from_unknown_region_probability
