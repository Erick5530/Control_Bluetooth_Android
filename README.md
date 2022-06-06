# Control_Bluetooth_Android

Aplicacion para establecer conexion con un modulo Bluetooth Hc-05 o Hc-06 (en modo esclavo), esta destinado para manejar un motor de direccion y un motor de arranque 
(izquierda, derecha;adelante,atras respectivamente) los valores son byte numericos enviados al puerto com de un Arduino UNO.

Las vistas para los análogos estan basados de la respuesta de este hilo en StackOverFlow https://stackoverflow.com/questions/18661107/android-creating-an-analogue-type-animated-image-for-movement
se modifico el codigo original para añadir un metodo a la interfaz para detectar que el analogo volvio al centro.

La vista animada del speedmeter esta implementado gracias a la libreria de ibrahimsn98/speedometer:https://github.com/ibrahimsn98/speedometer

El codigo para conectar a los dispositivos bluetooth se baso en el curso http://cursoandroidstudio.blogspot.com/2015/10/conexion-bluetooth-android-con-arduino.html
el codigo original esta hecho en java la de este proyecto lo pase a kotlin para poder reemplazar apis obsoletas asi como metodo que no funcionan, además, quite la parte 
en la que el arduino envia informacion y la app lo recibe y lee ya que no era requirido en la funcionalidad principal de mi aplicación.

el tamaño de la vista en general de la aplicacion esta diseñada de forma generica a excepcion del speedmeter, ya que esa parte se diseño para un tamaño de pantalla en especifico(Mi celular Redmi note 10 pro) 
ya que todas la pruebas y el uso final se haria en este.

