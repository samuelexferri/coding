import time
from math import ceil

import matplotlib.pyplot as plt

from classes import *
from judge import judge
from read import read
from write import write

path = './data/'


def solver(task):
    start = time.time()

    duration, n_intersec, n_streets, n_cars, bonus, streets, cars, intersections = read(path + task + '.txt')

    output = []

    for car in cars:
        car.score = 1 * len(car.streets) * (sum(street.time for street in car.streets))  # TODO

    street_score_min = 9999999999
    street_score_max = -1
    for i, street in streets.items():
        if len(street.cars) == 0:
            street.score = 0
        else:
            street.score = sum(car.score for car in street.cars) * len(street.cars) * (street.time)  # TODO

        if street_score_min > street.score:
            street_score_min = street.score
        if street_score_max < street.score:
            street_score_max = street.score

    score_norm_debug = []

    # Schedule
    for inter in intersections:
        bool = False
        schedule = Schedule([], inter)

        for street in inter.streets_in:
            COST_TIME = 35  # TODO
            score_norm = int(
                ceil(COST_TIME * (street.score - street_score_min) / (street_score_max - street_score_min)))  # TODO
            score_norm_debug.append(score_norm)

            if score_norm > 0:
                bool = True
                sp = SchedulePair(street, score_norm)
                schedule.pairs.append(sp)

        if bool:
            output.append(schedule)

    # Debug
    plt.plot(score_norm_debug)
    plt.show()

    temp = []

    for s in streets.values():
        temp.append(s.score)

    plt.plot(temp)
    plt.show()

    # print(street_score_max, street_score_min)
    # print(duration)

    write('./output/' + task + '.txt', output)

    score = judge(output)

    print(task, str(time.time() - start))
