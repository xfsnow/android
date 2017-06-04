package com.example.petstore;

import android.content.Context;
import android.support.v7.app.AlertDialog;
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
    //TODO 把 COGNITO_POOL_ID 换成你自己真实的ID
    public static final String COGNITO_POOL_ID = "xxxxxxxxxx";
    final MainActivity self = this;

    private void testPet() {
        // 使用Cognito进行验证
        AWSCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),  //当前活动的Context
                COGNITO_POOL_ID, // Cognito identity pool id
        //TODO 把这个区域设置成你的Cognito 所在的区域
                Regions.US_WEST_2
        );
        // 使用 ApiClientFactory 工厂方法来生成SDK 的客户端实例。
        ApiClientFactory factory = new ApiClientFactory().credentialsProvider(credentialsProvider);
        PetstoreClient client = factory.build(PetstoreClient.class);
        // 对API接口的调用已经封装成相应的方法，比如 /pets 接口的GET方法，对应 petsGet() 方法。
        // 具体的方法说明可以点击petsGet 方法名上的链接，跳转到PetstoreClient.java中查看源码。
        Pets pets = client.petsGet("dog", "1");
        // 列表中的每个项目可以用 get() 方法获取
        PetsItem item = pets.get(0);
        // 我们这里只使用Log.d() 方法打印出日志信息。
        Log.d(TAG, "one item in pet list: id=" + item.getId() + ", type=" + item.getType() + ", price=" + item.getPrice());
        // 调用详情接口
        String petId = "2";
        Pet pet = client.petsPetIdGet(petId);

        final String message = "pet: id=" + pet.getId() + ", type=" + pet.getType() + ", price=" + pet.getPrice();
        // 用弹出层演示在界面展示数据
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(self)
                        .setTitle("Pet")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show();
            }
        });
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
