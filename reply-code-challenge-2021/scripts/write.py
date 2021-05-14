def write(path, output):

    with open(path, "w") as file:

        file.write(str(len(output)) + '\n')

        for antenna in output:
            file.write(str(antenna.antenna_id) + ' ' + str(antenna.x) + ' ' + str(antenna.y) + '\n')

