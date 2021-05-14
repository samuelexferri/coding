import time

import numpy as np
from sklearn.cluster import KMeans

from read import read
from write import write

path = './input/'
task = 'data_scenarios_f_tokyo.in'

start = time.time()

output = []

buildings, antennas, reward = read(path + task)

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

# plt.scatter(X[:, 0], X[:, 1], label='True Position')
# plt.scatter(kmeans.cluster_centers_[:,0] , kmeans.cluster_centers_[:,1], color='black')

write('output/' + 'a.txt', output)

print(task, str(time.time() - start))
