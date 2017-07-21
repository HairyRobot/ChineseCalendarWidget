package hk.ccw.chinesecalendarwidget;

import android.app.Activity;
import android.os.Bundle;

public class WidgetProviderConfigureActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setResult(RESULT_CANCELED);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new WidgetProviderConfigureFragment()).commit();
	}
}
