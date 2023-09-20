import java.util.ArrayList;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;
import java.util.*;
//Mario, Gabriel e Edvard - OS FERA! =oD

public class OneApp {
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<FogDevice> mobiles = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	static double EEG_TRANSMISSION_TIME = 5.1;
	
	// Existem 4 sensores ligados a cada IoT Device
	// There's 4 sensors connected to each IoT Device
	
	// Sensors
	static double tempoDHT22 = 3.0;
	static double tempoZMPT101B = 1.0;
	static double tempoSCT013 = 1.0;
	
	// IoT Devices
	final static long MIPSIoT = 1500;
	final static int RAMIoT = 4000;
	final static long UPBWIoT = 150;
	final static long DOWNBWIoT = 150;
	final static double RatePerMipsIoT = 0;
	static double BusyPowerIoT = 90.0;
	static double IdelPowerIoT = 25.0;
	static double NetworkUsageIoT = 100;
	
	
	// ---------- Static Values ----------
	
	// Gateways
	final static long MIPSGat = 92500;
	final static int RAMGat = 64000;
	final static long UPBWGat = 1000;
	final static long DOWNBWGat = 1000 ;
	final static double RatePerMipsGat = 0.0;
	static double BusyPowerGat = 107.339;
	static double IdelPowerGat = 83.4333;

	// Proxy
	final static long MIPSProx = 2376800;
	final static int RAMProx = 4000;
	final static long UPBWProx = 10000;
	final static long DOWNBWProx = 10000 ;
	final static double RatePerMipsProx = 0.0;
	static double BusyPowerProx = 107.339;
	static double IdelPowerProx = 83.4333;

	// Cloud
	final static long MIPSCl = 6890500;
	final static int RAMCl = 32000;
	final static long UPBWCl = 16000;
	final static long DOWNBWCl = 16000 ;
	final static double RatePerMipsCl = 0.01;
	static double BusyPowerCl = 16*103;
	static double IdelPowerCl = 16*83.25;
	
	// Structure: number of IoTDevices/gateway
	// Estrutura: cada gateway possui x numero de IoTDevices
	static int numberOfGateways = 2;
	static int numberOfIoTDevices = 10;
	
	// Random generator
	//Gerador de aleatórios...
	static Random rdn = new Random();
	
	//public static void main(String[] args) {
	public void executar(String args[]) {
		
			try {
				//int rep = Integer.parseInt(args[5]);
				//for (int i = 1 ; i <= rep ; i++) {
				//	System.out.println("repeticao = " + i);
				
				//****************************************************************
				OneApp.IdelPowerIoT = Double.parseDouble(args[1]);	
				OneApp.BusyPowerIoT = Double.parseDouble(args[2]);
				OneApp.numberOfIoTDevices = Integer.parseInt(args[3]);
				OneApp.NetworkUsageIoT = Double.parseDouble(args[4]);
				
				
				
					String nomeArquivo = args[0]+".txt";
					System.out.println(nomeArquivo);
					FileOutputStream arquivoSaida = new FileOutputStream(nomeArquivo);
					PrintStream printStream = new PrintStream(arquivoSaida);
			        System.setOut(printStream);
			        
				//Redicionando a saída do console para um arquivo txt
				//https://www.programiz.com/java-programming/printstream#google_vignette
				//***********************************************************************
				
				//Log.printLine("Programa de Simulação OneApp...");
				//Log.printLine("Você está executando a instância " + args[0] + i + " da simulação.");
				
				Log.setDisabled(true);
				int num_user = 1; // number of cloud users
				Calendar calendar = Calendar.getInstance();
				boolean trace_flag = false; // mean trace events
	
				CloudSim.init(num_user, calendar, trace_flag);
	
				String appId0 = "HIT_MonitRessonancia";  //Monitoramento de Ressonancia Magnetica
				
				FogBroker broker0 = new FogBroker("broker_0");
	
				
				Application application0 = createApplication0(appId0, broker0.getId());
				application0.setUserId(broker0.getId());
				
				createFogDevices();
				
				createEdgeDevices0(broker0.getId(), appId0);
	
				
				ModuleMapping moduleMapping_0 = ModuleMapping.createModuleMapping(); // initializing a module mapping
				
				moduleMapping_0.addModuleToDevice("pre_proc_dados_iot", "cloud"); // fixing all instances of the Connector module to the Cloud
				//moduleMapping_0.addModuleToDevice("concentration_calculator", "cloud"); // fixing all instances of the Concentration Calculator module to the Cloud
				
				//moduleMapping_1.addModuleToDevice("connector_1", "cloud"); // fixing all instances of the Connector module to the Cloud
				//moduleMapping_1.addModuleToDevice("concentration_calculator_1", "cloud"); // fixing all instances of the Concentration Calculator module to the Cloud
				
				for(FogDevice device : fogDevices){
					if(device.getName().startsWith("m")){
						moduleMapping_0.addModuleToDevice("firmware_iot_borda", device.getName());  // fixing all instances of the Client module to the Smartphones
					}
				}
				
				Controller controller = new Controller("master-controller", fogDevices, sensors, actuators);
				
				controller.submitApplication(application0, new ModulePlacementMapping(fogDevices, application0, moduleMapping_0));
	
				TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());
				
				
				CloudSim.startSimulation();
				
				CloudSim.stopSimulation();
				
				printStream.close();
	            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	            System.out.println("A saída foi restaurada para o console.");
	            //Thread.sleep(1000);
	            //System.exit(0);
	            //System.gc();
				//Runtime.getRuntime().gc();
	            
				//}
			} catch (IOException e) {
				System.err.println("Erro ao redirecionar a saída para o arquivo: " + e.getMessage());
	        } catch (InterruptedException e) {
	            System.err.println("Erro durante a pausa: " + e.getMessage());
	        } catch (Exception e) {
				e.printStackTrace();
				Log.printLine("Unwanted errors happen");
			}
		
	}

	private static void createEdgeDevices0(int userId, String appId) {
		for(FogDevice iot : mobiles){
			String id = iot.getName();
			
			//Sensors - Temperature, Humidity, Electrical Current (A) and Tension (V)
			//Sensores - Temperatura, Umidade, Corrente (A) e Tensão (V)
			//Latência de sensor => de 0.1 a 6.0ms
			rdn.doubles(0.1, 6.0); // sensor latency
			
			// --------- Possivel alteracao na latencia ---------
			Sensor tempSensor = new Sensor("s-"+appId+"-"+id, "DHT22_temp", userId, appId, new DeterministicDistribution(tempoDHT22)); 
			sensors.add(tempSensor);
			tempSensor.setGatewayDeviceId(iot.getId());
			tempSensor.setLatency(rdn.nextDouble());
			
			Sensor umidSensor = new Sensor("s-"+appId+"-"+id, "DHT22_umid", userId, appId, new DeterministicDistribution(tempoDHT22)); 
			sensors.add(umidSensor);
			umidSensor.setGatewayDeviceId(iot.getId());
			umidSensor.setLatency(rdn.nextDouble());
			
			// Electrical Tension - ZMPT101B
			// Electrical Current SCT-013
			
			Sensor voltSensor = new Sensor("s-"+appId+"-"+id, "ZMPT101B_volt", userId, appId, new DeterministicDistribution(tempoZMPT101B)); 
			sensors.add(voltSensor);
			voltSensor.setGatewayDeviceId(iot.getId());
			voltSensor.setLatency(rdn.nextDouble());

			Sensor corrSensor = new Sensor("s-"+appId+"-"+id, "SCT-013_corr", userId, appId, new DeterministicDistribution(tempoSCT013)); 
			sensors.add(corrSensor);
			corrSensor.setGatewayDeviceId(iot.getId());
			corrSensor.setLatency(rdn.nextDouble());
			
		}
	}
	
	/**
	 * Creates the fog devices in the physical topology of the simulation.
	 * @param userId
	 * @param appId
	 */
	private static void createFogDevices() {
		// uplink and downlink in mbps
		// Latency for cloud => from 15ms to 250ms
		// Latencia para cloud => de 15ms a 250ms
		rdn.doubles(15.0, 250.0);
		
		FogDevice cloud = createFogDevice("cloud", MIPSCl, RAMCl, UPBWCl, DOWNBWCl, 0, RatePerMipsCl, BusyPowerCl, IdelPowerCl); // creates the fog device Cloud at the apex of the hierarchy with level=0
		cloud.setParentId(-1);
		
		// Latency for proxy => from 1ms to 15ms 
		// Latencia para proxy => de 1ms a 15ms
		rdn.doubles(1.0, 15.0);
		
		FogDevice proxy = createFogDevice("proxy-server",  MIPSProx, RAMProx, UPBWProx, DOWNBWProx, 1, RatePerMipsProx, BusyPowerProx, IdelPowerProx); // creates the fog device Proxy Server (level=1)
		proxy.setParentId(cloud.getId()); //associando o proxy com o cloud como componente pai
		
		// --------- Possivel alteracao na latencia ---------
		proxy.setUplinkLatency(rdn.nextDouble()); // latency in ms
		
		// Adding the devices into the environment
		// adicionando os dois dispositivos "no grafo"
		fogDevices.add(cloud);
		fogDevices.add(proxy);
		
		for(int i=0;i<numberOfGateways;i++){
			// adding a fog device for every Gateway in physical topology. 
			// The parent of each gateway is the Proxy Server
			addGw(i+"", proxy.getId()); 
		}
	}

	private static FogDevice addGw(String id, int parentId){
		FogDevice gateway = createFogDevice("g-"+id, MIPSGat, RAMGat, UPBWGat, DOWNBWGat, 1, RatePerMipsGat, BusyPowerGat, IdelPowerGat);
		fogDevices.add(gateway);
		gateway.setParentId(parentId);
		
		// --------- Possivel alteracao na latencia ---------
		// Latency for gateway => from 1ms to 15ms 
		// Latencia para gateway => de 1ms a 15ms
				rdn.doubles(1.0, 4.0);
		
		gateway.setUplinkLatency(rdn.nextDouble()); // latency of connection between gateways and proxy server is 4 ms
		for(int i=0;i<numberOfIoTDevices;i++){
			String iotId = id+"-"+i;
			FogDevice iotdev = addMobile(iotId, gateway.getId()); // adding mobiles to the physical topology. Smartphones have been modeled as fog devices as well.
			iotdev.setUplinkLatency(rdn.nextDouble()); // latency of connection between the smartphone and proxy server is 4 ms
			fogDevices.add(iotdev);
		}
		return gateway;
	}
	
	private static FogDevice addMobile(String id, int parentId){
		FogDevice iotdev = createFogDevice("m-"+id, MIPSIoT, RAMIoT, UPBWIoT, DOWNBWIoT, 3, RatePerMipsIoT, BusyPowerIoT, IdelPowerIoT);
		iotdev.setParentId(parentId);
		mobiles.add(iotdev);
		/*Sensor eegSensor = new Sensor("s-"+id, "EEG", userId, appId, new DeterministicDistribution(EEG_TRANSMISSION_TIME)); // inter-transmission time of EEG sensor follows a deterministic distribution
		sensors.add(eegSensor);
		Actuator display = new Actuator("a-"+id, userId, appId, "DISPLAY");
		actuators.add(display);
		eegSensor.setGatewayDeviceId(mobile.getId());
		eegSensor.setLatency(6.0);  // latency of connection between EEG sensors and the parent Smartphone is 6 ms
		display.setGatewayDeviceId(mobile.getId());
		display.setLatency(1.0);  // latency of connection between Display actuator and the parent Smartphone is 1 ms
		*/		
		return iotdev;
	}
	
	/**
	 * Creates a vanilla fog device
	 * @param nodeName name of the device to be used in simulation
	 * @param mips MIPS
	 * @param ram RAM
	 * @param upBw uplink bandwidth
	 * @param downBw downlink bandwidth
	 * @param level hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @return
	 */
	private static FogDevice createFogDevice(String nodeName, long mips,
			int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) {
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 1000000; // host storage
		int bw = 10000;

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new StreamOperatorScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
				arch, os, vmm, host, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		FogDevice fogdevice = null;
		try {
			fogdevice = new FogDevice(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fogdevice.setLevel(level);
		return fogdevice;
	}

	/**
	 * Function to create the EEG Tractor Beam game application in the DDF model. 
	 * @param appId unique identifier of the application
	 * @param userId identifier of the user of the application
	 * @return
	 */
	@SuppressWarnings({"serial" })
	private static Application createApplication0(String appId, int userId){
		
		Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)
				
		application.addAppModule("firmware_iot_borda", 10); // adding module Client to the application model
		application.addAppModule("pre_proc_dados_iot", 10); // adding module Concentration Calculator to the application model
		//application.addAppModule("connector", 10); // adding module Connector to the application model
		
		/*
		 * Connecting the application modules (vertices) in the application model (directed graph) with edges
		 */
		//if(EEG_TRANSMISSION_TIME==10)
		//	application.addAppEdge("EEG", "client", 2000, 500, "EEG", Tuple.UP, AppEdge.SENSOR); // adding edge from EEG (sensor) to Client module carrying tuples of type EEG
		//else
		
		//application.addAppEdge("firmware_iot_borda", "client", 1000, 100, "firmware_iot_borda", Tuple.UP, AppEdge.SENSOR);
		//Parametrização do CPULength para os dispositivos IoT
		rdn.doubles(50.0, 500.0);
		
		
		application.addAppEdge("DHT22_temp", "firmware_iot_borda", rdn.nextDouble(), OneApp.NetworkUsageIoT, "_SENSOR", Tuple.UP, AppEdge.SENSOR);
		application.addAppEdge("DHT22_umid", "firmware_iot_borda", rdn.nextDouble(), OneApp.NetworkUsageIoT, "_SENSOR", Tuple.UP, AppEdge.SENSOR);
		application.addAppEdge("ZMPT101B_volt", "firmware_iot_borda", rdn.nextDouble(), OneApp.NetworkUsageIoT, "_SENSOR", Tuple.UP, AppEdge.SENSOR);
		application.addAppEdge("SCT-013_corr", "firmware_iot_borda", rdn.nextDouble(), OneApp.NetworkUsageIoT, "_SENSOR", Tuple.UP, AppEdge.SENSOR);
		
		application.addAppEdge("firmware_iot_borda", "pre_proc_dados_iot", 100, rdn.nextDouble(), 100, "DADOS_IOT_BORDA", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("pre_proc_dados_iot", "firmware_iot_borda", 500, rdn.nextDouble(), 1000, "DADOS_IOT_CLOUD", Tuple.DOWN, AppEdge.MODULE);
		//application.addAppEdge("client", "concentration_calculator", 3500, 500, "_SENSOR", Tuple.UP, AppEdge.MODULE); // adding edge from Client to Concentration Calculator module carrying tuples of type _SENSOR
		//application.addAppEdge("concentration_calculator", "connector", 100, 1000, 1000, "PLAYER_GAME_STATE", Tuple.UP, AppEdge.MODULE); // adding periodic edge (period=1000ms) from Concentration Calculator to Connector module carrying tuples of type PLAYER_GAME_STATE
		//application.addAppEdge("concentration_calculator", "client", 14, 500, "CONCENTRATION", Tuple.DOWN, AppEdge.MODULE);  // adding edge from Concentration Calculator to Client module carrying tuples of type CONCENTRATION
		//application.addAppEdge("connector", "client", 100, 28, 1000, "GLOBAL_GAME_STATE", Tuple.DOWN, AppEdge.MODULE); // adding periodic edge (period=1000ms) from Connector to Client module carrying tuples of type GLOBAL_GAME_STATE
		//application.addAppEdge("client", "DISPLAY", 1000, 500, "SELF_STATE_UPDATE", Tuple.DOWN, AppEdge.ACTUATOR);  // adding edge from Client module to Display (actuator) carrying tuples of type SELF_STATE_UPDATE
		//application.addAppEdge("client", "DISPLAY", 1000, 500, "GLOBAL_STATE_UPDATE", Tuple.DOWN, AppEdge.ACTUATOR);  // adding edge from Client module to Display (actuator) carrying tuples of type GLOBAL_STATE_UPDATE
		
		/*
		 * Defining the input-output relationships (represented by selectivity) of the application modules. 
		 */
		application.addTupleMapping("firmware_iot_borda", "_SENSOR", "DADOS_IOT_BORDA", new FractionalSelectivity(0.9)); // 0.9 tuples of type _SENSOR are emitted by Client module per incoming tuple of type EEG 
		application.addTupleMapping("pre_proc_dados_iot", "DADOS_IOT_BORDA", "DADOS_PROCESSADOS", new FractionalSelectivity(1.0)); // 1.0 tuples of type SELF_STATE_UPDATE are emitted by Client module per incoming tuple of type CONCENTRATION 
		//application.addTupleMapping("concentration_calculator", "_SENSOR", "CONCENTRATION", new FractionalSelectivity(1.0)); // 1.0 tuples of type CONCENTRATION are emitted by Concentration Calculator module per incoming tuple of type _SENSOR 
		//application.addTupleMapping("client", "GLOBAL_GAME_STATE", "GLOBAL_STATE_UPDATE", new FractionalSelectivity(1.0)); // 1.0 tuples of type GLOBAL_STATE_UPDATE are emitted by Client module per incoming tuple of type GLOBAL_GAME_STATE 
	
		/*
		 * Defining application loops to monitor the latency of. 
		 * Here, we add only one loop for monitoring : EEG(sensor) -> Client -> Concentration Calculator -> Client -> DISPLAY (actuator)
		 */
		final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("DHT22_temp");add("DHT22_umid");add("ZMPT101B_volt");add("SCT-013_corr");add("firmware_iot_borda");}});
		//final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("DHT22_temp");add("firmware_iot_borda");}});
		List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
		application.setLoops(loops);
		System.out.println("Criou todos os comandos de aplica��o...");
		return application;
	}
	
}