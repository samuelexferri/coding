from multiprocessing import Process

from solver import solver, kmeans

if __name__ == '__main__':

    tasks = [
        'data_scenarios_a_example.in',
        'data_scenarios_b_mumbai.in',
        'data_scenarios_c_metropolis.in',
        'data_scenarios_d_polynesia.in',
        'data_scenarios_e_sanfrancisco.in',
        'data_scenarios_f_tokyo.in'
    ]

    for task in tasks:
        process = Process(target=solver, args=([task]))
        process.start()
