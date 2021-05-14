from multiprocessing import Process

from solver import solver

if __name__ == '__main__':

    tasks = [
        'a',
        'b',
        'c',
        'd',
        'e',
        'f'
    ]

    for task in tasks:
        process = Process(target=solver, args=([task]))
        process.start()
