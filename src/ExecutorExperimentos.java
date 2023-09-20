 
public class ExecutorExperimentos {
	
	public static void main(String args[]) {
		
		// Experimento 1 - Repeticoes (10 repeticoes por experimento)
			
		// Esses valores sao passados atraves do vetor de entrada de metodo args do metodo main...
		// Syntax: argumentos[nameFile, powerIdle, powerBusy, numIoTs, networkUsage]
		String argumentos[] = {"exp8r9", "50", "180", "75", "1000"};
		OneApp exp1 = new OneApp();
		exp1.executar(argumentos);
		
		//***************************************************************
		
	}
	
}
