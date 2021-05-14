from classes import *


def split_line(string):
    return [char for char in string]


def read(path):
    with open(path, 'r') as file:

        buildings = []
        antennas = []

        width, height = [int(value) for value in file.readline().strip().split(" ")]

        num_buildings, num_antennas, reward = [int(value) for value in file.readline().strip().split(" ")]

        for i in range(num_buildings):
            x, y, latency, speed = [int(value) for value in file.readline().strip().split(" ")]
            buildings.append(Building(x, y, latency, speed))

        for i in range(num_antennas):
            antenna_range, speed = [int(value) for value in file.readline().strip().split(" ")]
            antennas.append(Antenna(i, antenna_range, speed))

    return buildings, antennas, reward, width, height
