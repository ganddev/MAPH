#include <eHealth.h>
#include <SoftwareSerial.h>

#define BT_TX 0 //4
#define BT_RX 1 //5

SoftwareSerial mySerial(BT_RX, BT_TX);
char val, tmp;

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
  
  durchlauf += 1;
  
  if ( Serial.available() ){
    val = Serial.read();
  //  Serial.write( mySerial.read() );
  }
  if(val != tmp){
    if ( val == '1' ){
    
      digitalWrite(13, HIGH);

      //Serial.print("Durchlauf: ");
      Serial.print(durchlauf);  
      //Serial.println();
      Serial.print(";");
      //Serial.print("Hautleitfaehigkeit (conductance): ");
      Serial.print(conductance);
      //Serial.println(val);
      Serial.print("\r\n");
      //mySerial.write( "verstanden" );
      //val = '\0';
    }
    else {
      Serial.print("inkorrekt\r\n");
      //Serial.println("\r\n");
      digitalWrite(13, LOW);
      //val = '\0';
    }  
    tmp = val;
  /*durchlauf += 1;
  Serial.print("Durchlauf: ");
  Serial.println(durchlauf);  
  Serial.println();
  Serial.print("Hautleitfaehigkeit (conductance): ");
  Serial.println(conductance);
  Serial.print("Widerstand (resistance): ");
  Serial.println(resistance);
  Serial.print("Spannung Haut (conductance voltage): ");
  Serial.println(conductanceVol);
  Serial.println();*/
  // wait for a second  
  delay(1000); 
  }
}  
