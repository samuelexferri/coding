class Building:

    def __init__(self, x, y, latency_weight, speed_weight):
        self.x = x
        self.y = y
        self.latency_weight = latency_weight
        self.speed_weight = speed_weight


class Antenna:

    def __init__(self, antenna_id, antenna_range, speed):
        self.antenna_id = antenna_id
        self.antenna_range = antenna_range
        self.speed = speed
        self.x = None
        self.y = None
