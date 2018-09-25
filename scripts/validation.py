def assert_positive(name, value, allow_zero=False):
    if value < 0 or (value == 0 and not allow_zero):
        raise ValueError("{0} value must be {1} positive, found {2}".format(name.capitalize(), "" if allow_zero else "strictly", value))


def assert_in_range(name, value, lower, upper):
    assert lower < upper
    if value < lower or value > upper:
        raise ValueError("{0} value must be in [{1}, {2}], found {3}".format(name, lower, upper, value))


def assert_in_list(value, list):
    if value not in list:
        raise ValueError("Value {0} not between supported values: {1}".format(value, list))


def assert_is_instance(obj, cls):
    if not issubclass(type(obj), cls):
        raise ValueError("{0} is not an instance of {1}".format(obj, cls))
