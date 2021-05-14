import time

import numpy as np
from sklearn.cluster import KMeans

from read import read
from write import write

path = './input/'


def solver(task):
    print(task)
    start = time.time()

    output = []

    buildings, antennas, reward, width, height = read(path + task)

    antennas.sort(key=lambda x: x.speed * x.antenna_range, reverse=True)
    buildings.sort(key=lambda x: x.speed_weight, reverse=True)

    window = 15  # TODO Sliding

    grid = np.zeros((height, width))

    # Solo per il dataset B
    if task == 'data_scenarios_b_mumbai.in':
        antennas.sort(key=lambda x: x.antenna_range, reverse=True)

        coordinate_b = [100, 100, 300, 100, 100, 300, 300, 300]

        for i in range(4):
            a = antennas[0]
            a.x = coordinate_b[0]
            a.y = coordinate_b[1]
            coordinate_b = coordinate_b[2:]
            output.append(a)
            antennas = antennas[1:]

        antennas.sort(key=lambda x: x.speed, reverse=True)

    # Solo per il dataset D
    if task == 'data_scenarios_d_polynesia.in':
        X = np.array([[int(building.x), (building.y)] for building in buildings])

        kmeans = KMeans(n_clusters=len(antennas), max_iter=1)
        kmeans.fit(X)

        centroids_x = [int(value[0]) for value in kmeans.cluster_centers_]
        centroids_y = [int(value[1]) for value in kmeans.cluster_centers_]

        antennas.sort(key=lambda x: x.antenna_range, reverse=True)

        antennas_grande_range = antennas[:200]
        antennas = antennas[200:]

        for i in range(len(antennas_grande_range)):
            a = antennas_grande_range[i]
            a.x = centroids_x[i]
            a.y = centroids_y[i]
            output.append(a)

        for b in buildings:
            for i in range(len(antennas_grande_range)):
                if b.x == antennas_grande_range[i].x and b.y == antennas_grande_range[i].y:
                    buildings.remove(b)

        antennas.sort(key=lambda x: x.speed, reverse=True)

    # Funzione position
    def position(antenna, grid, width, height):
        r = antenna.antenna_range

        for i in range(antenna.antenna_range + 1):
            x_min = max(antenna.x - r + i, 0)
            x_max = min(antenna.x + r + 1 - i, width - 1)

            y_min = min(antenna.y + i, height - 1)
            y_max = max(antenna.y - i, 0)

            grid[y_min, x_min:x_max] = 1
            grid[y_max, x_min:x_max] = 1

            # print(antenna.y + i, height)

        return grid

    # Maggiore di window
    while len(antennas) > window and len(buildings) > window:
        candidates = antennas[:window]

        for i in range(len(candidates)):
            building = buildings[0]
            candidates[i].x = building.x
            candidates[i].y = building.y
            output.append(candidates[i])

            grid = position(candidates[i], grid, width, height)

            buildings.remove(building)

        for antenna in candidates:
            antennas.remove(antenna)

        for b in buildings:
            if grid[b.y][b.x] != 0:
                buildings.remove(b)

    # Minore di windows
    if len(buildings) != 0 and len(antennas) != 0:
        while len(buildings) > 0 and len(antennas) > 0:
            building = buildings[0]
            antenna = antennas[0]

            antenna.x = building.x
            antenna.y = building.y
            output.append(antenna)

            buildings.remove(building)
            antennas.remove(antenna)

    # Antenne rimaste
    if len(antennas) != 0:
        print('Sono rimaste antenne! ' + task)

    # Edifici coperti rimasti
    if len(buildings) == 0:
        print('Tutti gli edifici coperti! ' + task)
    else:
        print('Non tutti gli edifici coperti! ' + task)

    write('output/' + str(task.split('.')[0]) + '.txt', output)

    print(task, str(time.time() - start))


def kmeans(task):
    start = time.time()

    output = []

    buildings, antennas, reward, width, height = read(path + task)

    X = np.array([[int(building.x), (building.y)] for building in buildings])

    kmeans = KMeans(n_clusters=len(antennas), max_iter=1)
    kmeans.fit(X)

    centroids_x = [int(value[0]) for value in kmeans.cluster_centers_]
    centroids_y = [int(value[1]) for value in kmeans.cluster_centers_]

    antennas.sort(key=lambda antenna: antenna.speed, reverse=True)

    for i in range(len(centroids_x)):
        antennas[i].x = centroids_x[i]
        antennas[i].y = centroids_y[i]
        output.append(antennas[i])

    write('output/' + str(task.split('.')[0]) + '.txt', output)

    print(task, str(time.time() - start))
