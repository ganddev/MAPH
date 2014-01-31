package de.htwberlin.liar.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.htwberlin.liar.R;

public class HelpDialog extends DialogFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_galvanic_help, container, false);
	}
	
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceSate){
		
		final Dialog dialog = super.onCreateDialog(savedInstanceSate);
		dialog.setTitle(R.string.galvanic_help_title);
		return dialog;
	}
}
