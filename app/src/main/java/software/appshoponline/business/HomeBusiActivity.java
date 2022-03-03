package software.appshoponline.business;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import software.appshoponline.R;
import software.appshoponline.business.fragments.AccountBusiFragment;
import software.appshoponline.business.fragments.ChatsBusiFragment;
import software.appshoponline.business.fragments.HomeBusiFragment;
import software.appshoponline.business.fragments.StatisticBusiFragment;

public class HomeBusiActivity extends AppCompatActivity {

    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_busi);

        Fragment frgHomeBusi = new HomeBusiFragment();
        Fragment frgChatsBusi = new ChatsBusiFragment();
        Fragment frgStatisticBusi = new StatisticBusiFragment();
        Fragment frgAccountBusi = new AccountBusiFragment();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frg_container_business, frgHomeBusi);
        transaction.commit();
        BottomNavigationView btnNavBusi = findViewById(R.id.btnNavigationViewBusiness);
        btnNavBusi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()){
                    case R.id.action_homebusi:
                        transaction.replace(R.id.frg_container_business, frgHomeBusi);
                        break;
                    case R.id.action_chatsbusi:
                        transaction.replace(R.id.frg_container_business, frgChatsBusi);
                        break;
                    case R.id.action_statisticbusi:
                        transaction.replace(R.id.frg_container_business, frgStatisticBusi);
                        break;
                    case R.id.action_accountbusi:
                        transaction.replace(R.id.frg_container_business, frgAccountBusi);
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }
}