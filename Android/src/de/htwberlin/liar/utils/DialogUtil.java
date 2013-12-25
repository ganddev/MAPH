package de.htwberlin.liar.utils;

import de.htwberlin.liar.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {
	
	private DialogUtil(){};
	
	public static void showMessageDialog(Context parent, CharSequence message){
		AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
		builder.create().show();
	}
	
	public static void showMessageDialog(Context parent, int resid){
		showMessageDialog(parent, parent.getString(resid));
	}
}
