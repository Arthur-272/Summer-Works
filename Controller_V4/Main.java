package Controller_V4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Radhey\\OneDrive\\Desktop\\Data.txt"));
        System.out.println("Enter the total FOG NODE: ");
        int total_fog_node = scanner.nextInt();
        System.out.println("----------Configuring the FOG Nodes----------");
        FOGNODE fogNode[] = new FOGNODE[total_fog_node];
        for(int i=0;i<fogNode.length;i++){
            fogNode[i] = new FOGNODE(i);
        }
        System.out.println("Enter the Requests in the form of [Type,Latitude,Longitude]");
        System.out.println("Type is as follows:\n1. Video\n2. Audio\n3.Text\n");
        scanner.nextLine();
        String request = br.readLine();
        for(int i=0;request!=null;i++){
            //displayStatus(fogNode); To show or not?
            if(!RequestAllocationInFOGNODE(request,i, fogNode)){
                System.out.println("The Request_"+i+" is handed over to Cloud... [" + request + "]");
            }
            request = br.readLine();
        }
        System.out.println("\n\nSIMULATION COMPLETED!!!");
    }

    public static void ResourcesDeAllocation(String request[], FOGNODE fog[]){
        //Have a hunch but confused how to implement...
    }

    public static double distanceBetweenFogAndRequest(double x1, double y1, double x2, double y2){
        double result = Math.pow((Math.pow((x2-x1),2) + Math.pow((y2-y1),2)), 0.5);
        return result;
    }

    public static double[][] arrangeAccordingToDistance(FOGNODE fog[], String request){
        double latitude = getLatitude(request);
        double longitude = getLongitude(request);
        double dist[][] = new double[fog.length][2];
        for(int i=0;i<fog.length;i++){
            dist[i][0] = distanceBetweenFogAndRequest(latitude,longitude,fog[i].latitude,fog[i].longitude);
            dist[i][1] = i;
        }
        for(int i=0;i<fog.length;i++){
            for(int j=0;j<fog.length-1-i;j++){
                if(dist[j][0] > dist[j+1][0]){
                    double td = dist[j][0];
                    double ti = dist[j][1];

                    dist[j][0] = dist[j+1][0];
                    dist[j+1][0] = td;

                    dist[j][1] = dist[j+1][1];
                    dist[j+1][1] = ti;
                }
                else if(dist[j][0] == dist[j+1][0]){
                    if(fog[j].freeRam < fog[j+1].freeRam){
                        double ti = dist[j][1];
                        dist[j][1] = dist[j+1][1];
                        dist[j+1][1] = ti;
                    }
                }
            }
        }
        return dist;
    }

    public static boolean RequestAllocationInFOGNODE(String request, int requestId, FOGNODE fog[]){
        boolean flag = false;
        double reqResources = getRequiredResources(request);
        double distance[][] = arrangeAccordingToDistance(fog, request);
        int i;
        for(i=0;i<distance.length;i++){
            if(fog[(int)distance[i][1]].freeRam >= reqResources ){
                System.out.println("The Request_"+requestId+" is assigned to FOGNODE_"+(int)distance[i][1] +  "[" + request + "]");
                fog[(int)distance[i][1]].setFreeRam(fog[(int)distance[i][1]].RAM - fog[(int)distance[i][1]].usedRam - reqResources);
                fog[(int)distance[i][1]].setUsedRam(fog[(int)distance[i][1]].usedRam + reqResources);
                fog[(int)distance[i][1]].setReq(requestId);
                break;
            }
        }
        if(i == distance.length){
            return false;
        }
        else{
            return true;
        }
    }

    public static void displayStatus(FOGNODE fog[]){
        for(int i=0;i<fog.length;i++){
            System.out.println("FOGNODE_"+i);
            System.out.println("The Total RAM: " + fog[i].RAM);
            System.out.println("The free RAM: " + fog[i].freeRam);
            System.out.println("The used RAM: " + fog[i].usedRam);
            System.out.println("Latitude: " + fog[i].latitude);
            System.out.println("Longitude: " + fog[i].longitude);
            int req[] = fog[i].getReq();
            System.out.print("Total Requests going on (Request Ids): ");
            for(int x : req){
                System.out.print(x + ", ");
            }
            System.out.println();
        }
    }

    public static double getRequiredResources(String request){
        double type = getType(request);
        if(type == 1){          //Video
            return 512;
        }
        else if(type == 2){     //Audio
            return 256;
        }
        else if(type == 3){     //Text
            return 128;
        }
        else{
            return 0;
        }
    }

    public static double getLatitude(String request){
        String temp[] = request.split(",");
        return Double.parseDouble(temp[1]);
    }

    public static double getLongitude(String request){
        String temp[] = request.split(",");
        return Double.parseDouble(temp[2]);
    }

    public static double getType(String request){
        String temp[] = request.split(",");
        return Double.parseDouble(temp[0]);
    }
}
