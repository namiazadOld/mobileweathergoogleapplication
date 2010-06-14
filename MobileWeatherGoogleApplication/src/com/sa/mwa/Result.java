package com.sa.mwa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class Result extends Activity {

	private Button backButton;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.result);

		this.setContentView(R.layout.result);

		backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				back();
			}
		});

		for (int i = 0; i < 10; i++) {

			/* Find Tablelayout defined in main.xml */
			TableLayout tl = (TableLayout) findViewById(R.id.resultTable);
			/* Create a new row to be added. */
			TableRow tr = new TableRow(this);
			tr.setId(200 + i);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			/* Create a Button to be the row-content. */
			TextView tv = new TextView(this);
			tv.setText("Dynamic Button" + i);
			tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			/* Add textview to row. */
			tr.addView(tv);
			/* Add row to TableLayout. */
			tl.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

	}

	public void back() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();

	}
}
