import os
import json


class Printable:
    def __init__(self, name=None, add_name=True):
        self.__name = self.__class__.__name__ if name is None else name
        self.__add_name = add_name

    @property
    def name(self):
        return self.__name

    def __repr__(self):
        return '_'.join(['{0}={1}'.format(k, v) for k, v in self.__flatten_dict(self.as_dict(flag=False)).items()])

    def as_dict(self, flag=True):
        result = {}
        if self.__add_name:
            result['name'] = self.__name

        result.update({k: self.__resolve_value(v, flag) for k, v in self.__dict__.items() if self.__accept(k, v, flag)})

        return result

    @staticmethod
    def __accept(k, v, flag):
        if k.startswith('_') or v is None:
            return False
        if not flag and k in ['repeat', 'categorical', 'selector']:
            return False
        return True

    def to_json(self):
        return json.dumps(self.as_dict(), sort_keys=True, indent=4, separators=(',', ': '), allow_nan=False)

    def dump_to_config_file(self, folder, add_name=False):
        if add_name:
            folder = os.path.join(folder, self.__repr__())

        if not os.path.exists(folder):
            os.makedirs(folder)

        config_file = os.path.join(folder, 'config.json')

        if not os.path.exists(config_file):
            with open(config_file, 'w+') as f:
                f.write(self.to_json())

    @staticmethod
    def __flatten_dict(d):
        flattened = {}
        for k, v in d.items():
            if isinstance(v, dict):
                if 'name' in v:
                    flattened[k] = v.pop('name')
                flattened.update(Printable.__flatten_dict(v))
            elif isinstance(v, tuple) or isinstance(v, list):
                flattened[k] = '+'.join([str(x) for x in v])
            elif k != 'name':
                flattened[k] = v
        return flattened

    @classmethod
    def __resolve_value(cls, value, flag):
        if isinstance(value, Printable):
            return value.as_dict(flag)

        if isinstance(value, list):
            if not value:
                return value
            if all((x is value[0] for x in value)):
                value = [value[0]]
            if flag:
                return [cls.__resolve_value(v, flag) for v in value]

        return value
