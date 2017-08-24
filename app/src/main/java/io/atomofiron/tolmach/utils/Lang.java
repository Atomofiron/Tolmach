package io.atomofiron.tolmach.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Lang implements Parcelable, Cloneable {
	public String code;
	public String name;

	private Lang(Lang lang) {
		this.code = lang.code == null ? "" : lang.code;
		this.name = lang.name == null ? "" : lang.name;
	}

	public Lang(String code, String name) {
		this.code = code == null ? "" : code;
		this.name = name == null ? "" : name;
	}

	private Lang(Parcel in) {
		code = in.readString();
		name = in.readString();
	}

	public static final Creator<Lang> CREATOR = new Creator<Lang>() {
		@Override
		public Lang createFromParcel(Parcel in) {
			return new Lang(in);
		}

		@Override
		public Lang[] newArray(int size) {
			return new Lang[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(name);
	}

	@Override
	public String toString() {
		return code+"-"+name;
	}

	public static Lang parce(Parcelable p) {
		return (Lang) p;
	}

	public static ArrayList<Lang> parce(ArrayList<Parcelable> list) {
		ArrayList<Lang> langs = new ArrayList<>();
		for (Parcelable p : list)
			langs.add((Lang) p);

		return langs;
	}

	public static String[] getArryString(ArrayList<Lang> langs) {
		String[] list = new String[langs.size()];
		for (int i = 0; i < list.length; i++)
			list[i] = langs.get(i).name;

		return list;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass() && code.equals(((Lang) obj).code);
	}

	@Override
	protected Lang clone() {
		return new Lang(this);
	}
}