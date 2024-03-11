package com.example.unityapp;

import android.content.Context;
import android.content.res.Resources;

public class CityUtils {
    public String[] updateCitySpinner(Context context, int statePosition) {
        String[] cityOptions;
        Resources resources = context.getResources();
        switch (statePosition) {
            case 0:
                cityOptions = resources.getStringArray(R.array.selectoptions);
                break;
            case 1:
                cityOptions = resources.getStringArray(R.array.AndamanNicobar);
                break;
            case 2:
                cityOptions = resources.getStringArray(R.array.Andhrapradesh);
                break;
            case 3:
                cityOptions = resources.getStringArray(R.array.Arunachalpradesh);
                break;
            case 4:
                cityOptions = resources.getStringArray(R.array.Assam);
                break;
            case 5:
                cityOptions = resources.getStringArray(R.array.Bihar);
                break;
            case 6:
                cityOptions = resources.getStringArray(R.array.Chandigarh);
                break;
            case 7:
                cityOptions = resources.getStringArray(R.array.Chattisgarh);
                break;
            case 8:
                cityOptions = resources.getStringArray(R.array.Dadra_Nagar_Haveli_Daman_Diu);
                break;
            case 9:
                cityOptions = resources.getStringArray(R.array.Delhi_NCR);
                break;
            case 10:
                cityOptions = resources.getStringArray(R.array.Goa);
                break;
            case 11:
                cityOptions = resources.getStringArray(R.array.Gujarat);
                break;
            case 12:
                cityOptions = resources.getStringArray(R.array.Haryana);
                break;
            case 13:
                cityOptions = resources.getStringArray(R.array.Himachal_Pradesh);
                break;
            case 14:
                cityOptions = resources.getStringArray(R.array.Jammu_and_Kashmir);
                break;
            case 15:
                cityOptions = resources.getStringArray(R.array.Jharkhand);
                break;
            case 16:
                cityOptions = resources.getStringArray(R.array.Karnataka);
                break;
            case 17:
                cityOptions = resources.getStringArray(R.array.Kerala);
                break;
            case 18:
                cityOptions = resources.getStringArray(R.array.Ladakh);
                break;
            case 19:
                cityOptions = resources.getStringArray(R.array.Madhya_Pradesh);
                break;
            case 20:
                cityOptions = resources.getStringArray(R.array.Maharashtra);
                break;
            case 21:
                cityOptions = resources.getStringArray(R.array.Manipur);
                break;
            case 22:
                cityOptions = resources.getStringArray(R.array.Meghalaya);
                break;
            case 23:
                cityOptions = resources.getStringArray(R.array.Mizoram);
                break;
            case 24:
                cityOptions = resources.getStringArray(R.array.Nagaland);
                break;
            case 25:
                cityOptions = resources.getStringArray(R.array.Odisha);
                break;
            case 26:
                cityOptions = resources.getStringArray(R.array.Puducherry);
                break;
            case 27:
                cityOptions = resources.getStringArray(R.array.Punjab);
                break;
            case 28:
                cityOptions = resources.getStringArray(R.array.Rajasthan);
                break;
            case 29:
                cityOptions = resources.getStringArray(R.array.Sikkim);
                break;
            case 30:
                cityOptions = resources.getStringArray(R.array.Tamil_Nadu);
                break;
            case 31:
                cityOptions = resources.getStringArray(R.array.Telangana);
                break;
            case 32:
                cityOptions = resources.getStringArray(R.array.Tripura);
                break;
            case 33:
                cityOptions = resources.getStringArray(R.array.Uttar_Pradesh);
                break;
            case 34:
                cityOptions = resources.getStringArray(R.array.Uttarakhand);
                break;
            case 35:
                cityOptions = resources.getStringArray(R.array.West_Bengal);
                break;
            default:
                cityOptions = new String[0];
                break;
        }
        return cityOptions;
    }
}