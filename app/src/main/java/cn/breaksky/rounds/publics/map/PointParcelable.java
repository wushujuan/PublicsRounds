package cn.breaksky.rounds.publics.map;

import android.os.Parcel;
import android.os.Parcelable;

public class PointParcelable extends Point implements Parcelable {
	public PointParcelable() {

	}

	protected PointParcelable(Parcel in) {
		latitude = in.readFloat();
		longitude = in.readFloat();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(latitude);
		dest.writeFloat(longitude);
	}

}
