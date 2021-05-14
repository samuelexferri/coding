import matplotlib.pyplot as plt
import numpy as np


def analysis(task, buildings, antennas, reward):
    print('[' + task + ']')
    print('#B=' + str(len(buildings)) + ', #A=' + str(len(antennas)) + ', R=' + str(reward))

    a_ranges = []

    for a in antennas:
        a_ranges.append(a.antenna_range)

    a_ranges.sort(reverse=True)

    plt.figure()
    x = np.arange(len(a_ranges))
    plt.bar(x, height=a_ranges)
    plt.title("Antenna ranges")
    plt.xlabel("Antenne")
    plt.ylabel("Range")
    plt.savefig("output/" + task + "_a_ranges.png")

    a_speeds = []

    for a in antennas:
        a_speeds.append(a.speed)

    a_speeds.sort(reverse=True)

    plt.figure()
    x = np.arange(len(a_speeds))
    plt.bar(x, height=a_speeds)
    plt.title("Antenna speeds")
    plt.xlabel("Antenne")
    plt.ylabel("Speed")
    plt.savefig("output/" + task + "_a_speeds.png")

    b_latency = []

    for b in buildings:
        b_latency.append(b.latency_weight)

    b_latency.sort(reverse=True)

    plt.figure()
    x = np.arange(len(b_latency))
    plt.bar(x, height=b_latency)
    plt.title("Building latency")
    plt.xlabel("Buildings")
    plt.ylabel("Latency")
    plt.savefig("output/" + task + "_b_latency.png")

    b_speeds = []

    for b in buildings:
        b_speeds.append(b.speed_weight)

    b_speeds.sort(reverse=True)

    plt.figure()
    x = np.arange(len(b_speeds))
    plt.bar(x, height=b_speeds)
    plt.title("Building speeds")
    plt.xlabel("Buildings")
    plt.ylabel("Speed")
    plt.savefig("output/" + task + "_b_speeds.png")
