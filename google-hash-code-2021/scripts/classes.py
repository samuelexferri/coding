class Car:

    def __init__(self, streets):
        self.streets = streets
        self.street_in = streets[0]
        self.street_out = streets[-1]
        self.score = 0


class Street:

    def __init__(self, name, int_in, int_out, time):
        self.name = name
        self.int_in = int_in
        self.int_out = int_out
        self.time = time
        self.cars = []


class Intersection:

    def __init__(self, int_id, streets_in, streets_out):
        self.int_id = int_id
        self.streets_in = streets_in
        self.streets_out = streets_out


class Schedule:

    def __init__(self, pairs, intersection):
        self.pairs = pairs
        self.intersection = intersection


class SchedulePair:

    def __init__(self, street_in, duration):
        self.street_in = street_in
        self.duration = duration
