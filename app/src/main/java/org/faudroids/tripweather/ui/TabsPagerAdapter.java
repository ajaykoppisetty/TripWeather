package org.faudroids.tripweather.ui;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.maps.model.LatLng;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.WayPoint;
import org.faudroids.tripweather.weather.Forecast;

final class TabsPagerAdapter extends FragmentPagerAdapter {
	
	private final Context context;

	public TabsPagerAdapter(Context context, FragmentManager manager) {
		super(manager);
		this.context = context;
	}


	@Override
	public Fragment getItem(int position) {
		switch(position) {
			case 0: return MapFragment.createInstance(
					new LatLng(49.5896671, 11.0119429),
					"Erlangen",
					new LatLng(49.6839267, 10.9377587),
					"Hemhofen",
					"q{fnH_hwaA|@iDCYMo@\\k@PMF@nDzCx@n@\\PN@VFnAh@p@f@fA|AxAvB`AfAtCjCjDbCvBdAhB~@pB~A~ApBrCnEpCjEdAdAd@^t@b@tB~@n@`@v@~@`AnA~@lA^p@dBjE`@|@rBvCt@dAjBvDd@l@`@XVNn@Rt@F~@Fb@FhA`@^LlAT`DTx@NfBr@hClAjBnAv@v@~@jAf@lARv@xAdHh@dCTp@n@nA\\d@n@l@XPx@ZdAHb@@nBMn@C?HFPFJJBNAHOFYx@Fx@LjBJxMl@jFTnBRn@LjA\\bCp@`ALVHx@Bf@AlAMt@Qt@e@zBsAdF{CzAgAlBkAf@UdAa@|@WdASdBYN@b@IdCeA~B_BlBaB~@cAn@q@dCaDrEsG\\i@VWxFmF~EaEnYeVpDmCrBmAlFkCF?pAo@BDFDF@JGHM?W?ERQp@m@DM`@_@bCyCjDmD~@aA~@s@dAgAr@mAf@mAj@{@^Fz@Nb@Nn@XFJdAp@~@v@HHHSzAgDDWLUt@wBd@eA`@sAX}AV_A~BuM|@oExBwJd@sBp@kBrB_GtA}C|AkCh@y@xCcE~@mA`E{E`CmCvAmBpAkB|@uAjBcEr@iB~@kCtAsFTiAXqBz@qHRyCLiDFiDFgKB_R@gCJwDJsBJaBDu@XmCR{ALo@XgB`@oBb@}Ax@_CNi@j@wAj@sAlAsBtAyBlEuErDgD\\YDBvAqAl@m@FO|@_ApB{BvBkCpBoCV_@F?dCuDnAaCZcAd@yAr@{B`AuD|@aFZ}D^qICYHaBh@uK\\cIn@kPFuBBOq@Ke@QWIyA_AmC_BYYMYMc@Cg@?c@D]HULSx@i@`JN`EDpRNjQNdOLrDNf@FjB\\bAZ|CfAz@jAJXH`@@j@EbAAt@Ft@Rp@^d@d@d@TJJ?DEHIZu@fBeFlAcDxCuHdBqEVcAT}@t@uERaCHgBDmDAmBKoFS_HKqBI]YgIKYqEX]sFa@uE`CqB");
			case 1: return GraphFragment.createInstance(
					new Forecast[]{
							new Forecast(new WayPoint(0, 0), 20, 1425155478),
							new Forecast(new WayPoint(0, 0), 10, 1425165478),
							new Forecast(new WayPoint(0, 0), 14, 1425175478),
							new Forecast(new WayPoint(0, 0), 0, 1425158478),
							new Forecast(new WayPoint(0, 0), -2, 1425195478)
					});
		}
		return null;
	}


	@Override
	public int getCount() {
		return 2;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		switch(position) {
			case 0: return context.getString(R.string.tab_map);
			case 1: return context.getString(R.string.tab_temperature);
		}
		return null;
	}
}
