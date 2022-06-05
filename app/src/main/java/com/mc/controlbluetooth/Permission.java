package com.mc.controlbluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permission {


    private String[] globalPermissions;

    private final Activity act;


    public Permission(Activity act) {

        this.act = act;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            globalPermissions = new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
        }
    }

    public boolean checkPermissions() {

        System.out.println("Solicitand permisos");

        int countPermissions = 0;
        /*
          Hacer un recorrido por los permisos globales para revisar que permisos se han otorgado,
          la variable countPermissions se incrementa si no tenemos otorgado el permiso.
         */
        for (String globalPermission : globalPermissions) {
            if (ContextCompat.checkSelfPermission(act, globalPermission) != PackageManager.PERMISSION_GRANTED) {
                countPermissions++;
            }
        }

        /*
         * Arreglo de Strings del tamaño de permisos que no han sido otorgados.
         */
        String[] permissions = new String[countPermissions];
        countPermissions = 0;

        /*
         * Llenado del arreglo de Strings con los permisos que no fueron otorgados para
         * posteriormente pedirlos al usuario.
         */
        for (String globalPermission : globalPermissions) {
            if (ContextCompat.checkSelfPermission(act, globalPermission) != PackageManager.PERMISSION_GRANTED) {
                permissions[countPermissions++] = globalPermission;
            }
        }

        if (countPermissions > 0) {
            ActivityCompat.requestPermissions(act, permissions, 1);
            return false;
        }
        return true;
    }


}
