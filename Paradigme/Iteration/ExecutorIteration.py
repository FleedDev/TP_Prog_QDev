import os
import subprocess

def compile_java():
    java_files = [
        os.path.join("Paradigme", "Iteration", "Assignment102.java"),
    ]
    for java_file in java_files:
        if os.path.exists(java_file):
            subprocess.run(["javac", java_file], check=True)
        else:
            print(f"Fichier Java non trouvé : {java_file}")
            return

def run_java(total_count, num_workers):
    classpath = "C:/Users/maazn/Desktop/BUT/TP_Prog_QDev/out/production/TP_Prog_QDev"

    java_command = [
        "java", "-cp", classpath, "Paradigme.Iteration.Assignment102",
        str(total_count), str(num_workers)
    ]
    process = subprocess.Popen(java_command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = process.communicate()

    if process.returncode != 0:
        print(f"Erreur lors de l'exécution du programme Java: {stderr.decode()}")
    else:
        print(stdout.decode())

def main():
    compile_java()

    total_count = 48000000
    worker_list = [1, 2, 3, 4, 6, 8, 12]
    for i in range(5):
        for num_workers in worker_list:
            iterations_per_worker = total_count
            print(f"\nExécution avec {num_workers} workers (scalabilité forte) - {iterations_per_worker} itérations par worker")
            run_java(iterations_per_worker, num_workers)

    # print("\nTest de la scalabilité faible:")
    # for num_workers in worker_list:  # Utiliser la liste des workers
    #     total_iterations = total_count * num_workers  # Total d'itérations augmente avec le nombre de workers
    #     print(f"Scalabilité faible avec {total_iterations} itérations et {num_workers} worker(s)")
    #     run_java(total_iterations, num_workers)


if __name__ == "__main__":
    main()
