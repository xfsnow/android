package com.example.petstore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.regions.Regions;
import com.example.petstore.model.Pet;
import com.example.petstore.model.Pets;
import com.example.petstore.model.PetsItem;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "petstore_MainActivity";
    public static final String COGNITO_POOL_ID = "us-west-2:xxxxxx";

    private void testPet() {
        AWSCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),  // activity context
                COGNITO_POOL_ID, // Cognito identity pool id
                Regions.US_WEST_2 // region of Cognito identity pool
        );
        ApiClientFactory factory = new ApiClientFactory().credentialsProvider(credentialsProvider);
        PetstoreClient client = factory.build(PetstoreClient.class);
        Pets pets = client.petsGet("dog", "1");
        PetsItem item = pets.get(0);
        Log.d(TAG, "one item in pet list: id="+item.getId()+", type="+item.getType()+", price="+item.getPrice());
        String petId = "2";
        Pet pet = client.petsPetIdGet(petId);
        Log.d(TAG, "pet: id="+pet.getId()+", type="+pet.getType()+", price="+pet.getPrice());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread() {
            @Override
            public void run() {
                super.run();
                testPet();
            }
        }.start();
    }
}
