package com.affiliate.earning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NotificationsFragment extends Fragment {

    double in1 = 0;
    double i2 = 0;
    TextView edittext1;
    boolean add;
    boolean sub;
    boolean multiply;
    boolean divide;
    boolean remainder;
    boolean deci;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button buttonadd;
    Button buttonsub;
    Button buttonmul;
    Button buttondiv;
    Button buttonequ;
    Button buttondel;
    Button buttondot;
    Button buttonremainder;
    Button gotoScratch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, null);

        getActivity().setTitle("Calculator");

        button0 = (Button) view.findViewById(R.id.b0);
        button1 = (Button) view.findViewById(R.id.b1);
        button2 = (Button) view.findViewById(R.id.b2);
        button3 = (Button) view.findViewById(R.id.b3);
        button4 = (Button) view.findViewById(R.id.b4);
        button5 = (Button) view.findViewById(R.id.b5);
        button6 = (Button) view.findViewById(R.id.b6);
        button7 = (Button) view.findViewById(R.id.b7);
        button8 = (Button) view.findViewById(R.id.b8);
        button9 = (Button) view.findViewById(R.id.b9);
        buttondot = (Button) view.findViewById(R.id.bDot);
        buttonadd = (Button) view.findViewById(R.id.badd);
        buttonsub = (Button) view.findViewById(R.id.bsub);
        buttonmul = (Button) view.findViewById(R.id.bmul);
        buttondiv = (Button) view.findViewById(R.id.biv);
        buttonremainder = (Button) view.findViewById(R.id.BRemain);
        buttondel = (Button) view.findViewById(R.id.buttonDel);
        buttonequ = (Button) view.findViewById(R.id.buttoneql);
        gotoScratch = (Button) view.findViewById(R.id.gotoScratch);

        edittext1 = (TextView) view.findViewById(R.id.display);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "1");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "2");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "3");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "4");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "5");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "6");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "7");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "8");
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "9");
            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText(edittext1.getText() + "0");
            }
        });

        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext1.getText().length() != 0) {
                    in1 = Float.parseFloat(edittext1.getText() + "");
                    add = true;
                    deci = false;
                    edittext1.setText(null);
                }
            }
        });

        buttonsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext1.getText().length() != 0) {
                    in1 = Float.parseFloat(edittext1.getText() + "");
                    sub = true;
                    deci = false;
                    edittext1.setText(null);
                }
            }
        });

        buttonmul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext1.getText().length() != 0) {
                    in1 = Float.parseFloat(edittext1.getText() + "");
                    multiply = true;
                    deci = false;
                    edittext1.setText(null);
                }
            }
        });

        buttondiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext1.getText().length() != 0) {
                    in1 = Float.parseFloat(edittext1.getText() + "");
                    divide = true;
                    deci = false;
                    edittext1.setText(null);
                }
            }
        });

        buttonremainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext1.getText().length() != 0) {
                    in1 = Float.parseFloat(edittext1.getText() + "");
                    remainder = true;
                    deci = false;
                    edittext1.setText(null);
                }
            }
        });

        buttonequ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add || sub || multiply || divide || remainder) {
                    i2 = Float.parseFloat(edittext1.getText() + "");
                }

                if (add) {

                    edittext1.setText(in1 + i2 + "");
                    add = false;
                }

                if (sub) {

                    edittext1.setText(in1 - i2 + "");
                    sub = false;
                }

                if (multiply) {
                    edittext1.setText(in1 * i2 + "");
                    multiply = false;
                }

                if (divide) {
                    edittext1.setText(in1 / i2 + "");
                    divide = false;
                }
                if (remainder) {
                    edittext1.setText(in1 % i2 + "");
                    remainder = false;
                }
            }
        });

        buttondel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext1.setText("");
                in1 = 0.0;
                i2 = 0.0;
            }
        });

        buttondot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deci) {
                    //do nothing or you can show the error
                } else {
                    edittext1.setText(edittext1.getText() + ".");
                    deci = true;
                }

            }
        });

        gotoScratch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ScratchUI.class);
                startActivity(i);
            }
        });
        return view;
    }


}


