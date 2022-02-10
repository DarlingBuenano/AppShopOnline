package software.appshoponline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import software.appshoponline.client.AccountFragment;
import software.appshoponline.client.CarShopFragment;
import software.appshoponline.client.ChatsFragment;
import software.appshoponline.client.HomeFragment;
import software.appshoponline.client.LikesFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Fragment frgHome = new HomeFragment();
        Fragment frgChats = new ChatsFragment();
        Fragment frgCarShop = new CarShopFragment();
        Fragment frgLikes = new LikesFragment();
        Fragment frgAccount = new AccountFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frg_container_client, frgHome)
                .commit();
        BottomNavigationView btnNav = findViewById(R.id.btnNavigationViewClient);

        btnNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()){
                    case R.id.action_home:
                        transaction.replace(R.id.frg_container_client, frgHome);
                        break;
                    case R.id.action_chats:
                        transaction.replace(R.id.frg_container_client, frgChats);
                        break;
                    case R.id.action_shopcar:
                        transaction.replace(R.id.frg_container_client, frgCarShop);
                        break;
                    case R.id.action_likes:
                        transaction.replace(R.id.frg_container_client, frgLikes);
                        break;
                    case R.id.action_account:
                        transaction.replace(R.id.frg_container_client, frgAccount);
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }
}