from multiprocessing import Process

from solver import solverbucket, solver, analysis

if __name__ == '__main__':

    tasks = [
        'a_example',
        'b_little_bit_of_everything',
        'c_many_ingredients',
        'd_many_pizzas',
        'e_many_teams'
    ]

    anal = False
    sol = True
    solbuc = False

    # Analysis
    if anal:
        for task in tasks:
            process = Process(target=analysis, args=([task]))
            process.start()

    # Solver
    if sol:
        for task in tasks:
            process = Process(target=solver, args=([task]))
            process.start()

    # Solver
    if solbuc:
        for task in tasks:
            process = Process(target=solverbucket, args=([task]))
            process.start()
