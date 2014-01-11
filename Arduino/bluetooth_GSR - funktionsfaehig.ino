#include <eHealth.h>
#include <SoftwareSerial.h>

#define BT_TX 0 //4
#define BT_RX 1 //5

SoftwareSerial mySerial(BT_RX, BT_TX);


int durchlauf = 0;

void setup(){
  
  Serial.begin(9600);
  mySerial.begin(115200);
  pinMode(13, OUTPUT); 
  
}

void loop(){
  
  float conductance = eHealth.getSkinConductance();
  float resistance = eHealth.getSkinResistance();
  float conductanceVol = eHealth.getSkinConductanceVoltage();
  
  String bText; //text to send over bluetooth
  
  durchlauf += 1;
  
  bText += durchlauf;
  bText += ";";
  bText += (int) conductance;
  bText += ";";
  bText += (int long) resistance;  
  Serial.print(bText);
  Serial.print("\r\n");

  //bText += "b"; //just 4 debugging which kind of String does the Smartphone recieve
  //mySerial.print(bText);
  //mySerial.print("\r\n");
  
  // wait for a second  
  delay(1000); 
  
}  
