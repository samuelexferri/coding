def write(path, output):
    with open(path, "w") as file:
        # N. of intesections with schedules
        file.write(f"{len(output)}\n")

        for schedule in output:
            if not len(schedule.pairs):
                continue
            # Id of intersection
            file.write(f"{schedule.intersection.int_id}\n")
            # N. of schedule pairs
            file.write(f"{len(schedule.pairs)}\n")

            for sp in schedule.pairs:
                # Name of street and duration
                file.write(f"{sp.street_in.name} {sp.duration}\n")
