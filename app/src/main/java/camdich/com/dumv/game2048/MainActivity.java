package camdich.com.dumv.game2048;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements setDataa {
    private GridView gdvGame;
    private OVuongAdapter oVuongAdapter;
    private View.OnTouchListener listener;
    private float X, Y;
    private TextView txvPoint, txvMAX;
    private int vt1, vt2;

    private DatabaseReference getanh = null;
    private static FirebaseDatabase fbDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Data.getInstance().getMau(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        anhXa();
        khoiTao();
        setData();

        if (fbDatabase == null) {
            fbDatabase = FirebaseDatabase.getInstance();
            fbDatabase.setPersistenceEnabled(true);
        }
        if (getanh == null) {
            getanh = fbDatabase.getInstance().getReference();
        }

    }

    private void anhXa() {
        gdvGame = (GridView) findViewById(R.id.gdvGame);
        txvPoint = (TextView) findViewById(R.id.txvPoint);
        txvMAX = (TextView) findViewById(R.id.txvMAX);
    }

    private void khoiTao() {
        Data.getInstance().intt();
        oVuongAdapter = new OVuongAdapter(MainActivity.this, 0, Data.getInstance().getArr());

        listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Data.getInstance().setKhoaback(true);
                switch ((event.getAction())) {
                    case MotionEvent.ACTION_DOWN:
                        X = event.getX();
                        Y = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(event.getX() - X) > Math.abs(event.getY() - Y)) {
                            if (event.getX() > X) {
                                Data.getInstance().phai();
                                oVuongAdapter.notifyDataSetChanged();
                            } else {
                                Data.getInstance().trai();
                                oVuongAdapter.notifyDataSetChanged();
                            }
                        } else {

                            if (event.getY() > Y) {

                                Data.getInstance().xuong();
                                oVuongAdapter.notifyDataSetChanged();
                            } else {
                                Data.getInstance().len();
                                oVuongAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                }
                txvMAX.setText("" + Data.getInstance().getPoint());
                txvPoint.setText("" + Data.getInstance().getSoDiem(MainActivity.this));

                return true;
            }
        };

    }

    private boolean b = false;

    private void setData() {
        gdvGame.setAdapter(oVuongAdapter);
        gdvGame.setOnTouchListener(listener);
    }

    public void back(View v) {
        Data.getInstance().back();
        oVuongAdapter.notifyDataSetChanged();
        txvPoint.setText("" + Data.getInstance().getSoDiem(MainActivity.this));
        Data.getInstance().setKhoaback(false);
    }

    public void exchange(View v) {
        if (Data.getInstance().isKhoaChon()) {
            vt1 = -1;
            Toast.makeText(MainActivity.this, "Chọn Vị Trí 1", Toast.LENGTH_SHORT).show();
            Data.getInstance().setKhoaChon(false);
        } else {
            return;
        }
        //gdvGame
        gdvGame.setOnTouchListener(null);
        gdvGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (vt1 == -1) {
                    vt1 = position;
                    Toast.makeText(MainActivity.this, "Chọn Vị Trí 2 - " + vt1, Toast.LENGTH_SHORT).show();
                } else {
                    vt2 = position;

                    gdvGame.setAdapter(oVuongAdapter);
                    Data.getInstance().doiChoi(vt1, vt2);
                    oVuongAdapter.notifyDataSetChanged();
                    txvPoint.setText("" + Data.getInstance().getSoDiem(MainActivity.this));
                    //  Data.getInstance().setKhoaChon(true);
                    vt1 = -1;
                    gdvGame.setOnItemClickListener(null);
                    gdvGame.setOnTouchListener(listener);
                }
            }
        });
    }

    public void chuyen2ve4(View v) {
        Data.getInstance().chuyen();
        oVuongAdapter.notifyDataSetChanged();
        txvPoint.setText("" + Data.getInstance().getSoDiem(MainActivity.this));
    }

    @Override
    public void win() {
        if (b == false) {
            Toasty.normal(MainActivity.this, "Chúc Mừng Bạn Đã Thắng Cuộc", ContextCompat.getDrawable(MainActivity.this, R.drawable.aicap)).show();
            b = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
