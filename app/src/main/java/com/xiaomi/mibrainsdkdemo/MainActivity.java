package com.xiaomi.mibrainsdkdemo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaomi.ai.AsrRequest;
import com.xiaomi.ai.NlpRequest;
import com.xiaomi.ai.SpeechEngine;
import com.xiaomi.ai.SpeechError;
import com.xiaomi.ai.SpeechResult;
import com.xiaomi.ai.TtsRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MiBrainSDK_DEMO";
    SpeechEngine mEngine;
    EditText mInputText;
    TextView mOutputText;
    TextView mASRText;
    Button mCombineRequestButton;
    Button mCombineDataRequestButton;
    Button mASRRequestButton;
    Button mNLPRequstButton;
    Button mTTSRequestButton;
    Handler mHandler;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new UiHandler();
        Listeners.setHandler(mHandler);
        mEngine = MiBrainSDKHelper.getEngineInstance(this, MainActivity.this);
        if(mEngine == null) {
            Log.e(TAG, "init failed");
            return;
        }
        //mEngine.cleanAllUserLoginData(); //切换鉴权模式时需要调用该接口清空老数据
        mEngine.setAsrLisnter(Listeners.ASRListener);
        mEngine.setNlpListener(Listeners.NLPListener);
        mEngine.setTtsListener(Listeners.TTSListener);
        mEngine.setServiceEventListener(Listeners.ServiceEventListener);
        //mEngine.setInstructionListener(Listeners.InstructionListener); //内测功能，开发者不需要关注

        // 支持打点 仅 production 环境支持
        mEngine.setStartEventTrack(true);

        mInputText = findViewById(R.id.edt_input);
        mOutputText = findViewById(R.id.tv_output);
        mASRText = findViewById(R.id.tv_asr);

        mCombineRequestButton = findViewById(R.id.btn_combine);
        mCombineRequestButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mCombineRequestButton.onTouchEvent(motionEvent);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        startCombinedRequest();
                        //若使用外部数据模式，需要在开始请求后调用addBuffer接口
                        //add完毕后调用endSpeech接口
                        break;
                    case MotionEvent.ACTION_UP:
                        //非VAD模式下需要手动表明录音结束
                        //VAD模式下回自动判停
                        endASRRequest();
                }
                return true;
            }
        });

        mCombineDataRequestButton = findViewById(R.id.btn_combine_file);
        mCombineDataRequestButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mCombineDataRequestButton.onTouchEvent(motionEvent);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            InputStream s = AssetsHelper.getAssetsFile(MainActivity.this, "play_jay_song.raw");
                            startCombinedDataRequest();
                            //假设每次读4k
                            byte[] buffer = new byte[4096];
                            int readn = 0;
                            while((readn = s.read(buffer)) != -1) {
                                mEngine.addBuffer(buffer, 0, readn);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        endASRRequest();
                }
                return true;
            }
        });

        mASRRequestButton = findViewById(R.id.btn_asr);
        mASRRequestButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mASRRequestButton.onTouchEvent(motionEvent);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        startASRRequest();
                        //若使用外部数据模式，需要在开始请求后调用addBuffer接口
                        //add完毕后调用endSpeech接口
                        break;
                    case MotionEvent.ACTION_UP:
                        //非VAD模式下需要手动表明录音结束
                        //VAD模式下回自动判停
                        //endASRRequest();
                        //此处为云端VAD模式，不需要手动标记停止
                }
                return true;
            }
        });

        mNLPRequstButton = findViewById(R.id.btn_nlp);
        mNLPRequstButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startNLPRequest(mInputText.getText().toString());
            }
        });

        mTTSRequestButton = findViewById(R.id.btn_tts);
        mTTSRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTTSRequest(mInputText.getText().toString());
            }
        });

    }

    public void startASRRequest() {
        AsrRequest asrRequest = Requests.getASRRequest();
        mEngine.listenSpeech(asrRequest);
    }

    public void startASRDataRequest() {
        AsrRequest asrRequest = Requests.getASRDataRequest();
        mEngine.listenSpeech(asrRequest);
    }

    public void endASRRequest() {
        mEngine.endSpeech();
    }

    public void startNLPRequest(String query) {
        NlpRequest nlpRequest = Requests.getNLPRequest(query, true);
        mEngine.semanticsParse(nlpRequest);
    }

    public void startTTSRequest(String query) {
        TtsRequest ttsRequest = Requests.getTTSRequest(query);
        mEngine.speak(ttsRequest);
    }

    public void startCombinedRequest() {
        SpeechEngine.ParamBuilder b = Requests.getCombinedRequest();
        mEngine.startIntegrally(b);
    }

    public void startCombinedDataRequest() {
        SpeechEngine.ParamBuilder b = Requests.getCombinedDataRequest();
        mEngine.startIntegrally(b);
    }

    private class UiHandler extends Handler {
        private static final int MSG_UPDATE_ASR_PART = 1;
        private static final int MSG_UPDATE_ASR_RESULT = 2;
        private static final int MSG_UPDATE_NLP_RESULT = 3;
        private static final int MSG_UPDATE_ERROR = 7;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_ASR_PART: {
                    SpeechResult r = (SpeechResult) msg.obj;
                    mASRText.setText("");
                    mASRText.setText(r.getQuery());
                    break;
                }
                case MSG_UPDATE_ASR_RESULT: {
                    SpeechResult r = (SpeechResult) msg.obj;
                    mASRText.setText("");
                    mASRText.setText(r.getQuery());
                    break;
                }
                case MSG_UPDATE_NLP_RESULT: {
                    SpeechResult r = (SpeechResult) msg.obj;
                    mOutputText.setText("");
                    mOutputText.setText(r.getAnswerText());
                    break;
                }
                case MSG_UPDATE_ERROR: {
                    SpeechError error = (SpeechError) msg.obj;
                    mOutputText.setText("");
                    mOutputText.setText(error.getErrMsg());
                    break;
                }
            }
        }
    }
}

