package com.example.android.testproject;

import java.util.List;

/**
 * Created by Rayner on 9/4/17.
 */

public interface ServerCallback {
    void onSuccess (List<BuildingMessage> buildings);
}
