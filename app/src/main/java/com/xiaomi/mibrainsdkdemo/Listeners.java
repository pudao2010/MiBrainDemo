package com.xiaomi.mibrainsdkdemo;

import android.media.AudioTrack;
import android.os.Handler;
import android.util.Log;

import com.xiaomi.ai.AsrListener;
import com.xiaomi.ai.Instruction;
import com.xiaomi.ai.InstructionListener;
import com.xiaomi.ai.NlpListener;
import com.xiaomi.ai.PCMInfo;
import com.xiaomi.ai.ServiceEvent;
import com.xiaomi.ai.ServiceEventListener;
import com.xiaomi.ai.SpeechError;
import com.xiaomi.ai.SpeechResult;
import com.xiaomi.ai.TtsListener;

public class Listeners {
    private static final String TAG = "MiBrainSDK_Listeners";
    private static Handler mHandler;
    private static final int MSG_UPDATE_ASR_PART = 1;
    private static final int MSG_UPDATE_ASR_RESULT = 2;
    private static final int MSG_UPDATE_NLP_RESULT = 3;
    private static final int MSG_UPDATE_ERROR = 7;


    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    public static void notifyHandler(int msg, Object arg) {
        if(mHandler != null) {
            mHandler.removeMessages(msg);
            mHandler.obtainMessage(msg, arg).sendToTarget();
        }
    }

    public static AsrListener ASRListener = new AsrListener() {
        @Override
        public void onReadyForSpeech() {
            Log.d(TAG, "ASR onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "ASR onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "ASR onRmsChanged");

        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d(TAG, "ASR onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "ASR onEndOfSpeech");
        }

        @Override
        public void onError(SpeechError speechError) {
            //ASR请求错误时回调
            Log.d(TAG, "ASR onError " + speechError.getErrMsg());
            notifyHandler(MSG_UPDATE_ERROR, speechError);
        }

        @Override
        public void onResults(SpeechResult speechResult) {
            //ASR请求结果回调
            Log.d(TAG, "ASR onResults " + speechResult.getQuery());
            notifyHandler(MSG_UPDATE_ASR_RESULT, speechResult);
        }

        @Override
        public void onPartialResults(SpeechResult speechResult) {
            //ASR中间结果回调
            Log.d(TAG, "ASR onPartialResults " + speechResult.getQuery());
            notifyHandler(MSG_UPDATE_ASR_PART, speechResult);
        }

        @Override
        public void onEvent() {
            Log.d(TAG, "ASR onEvent");
        }

        @Override
        public void onFileStored(String s, String s1) {
            Log.d(TAG, "ASR onFileStored");
        }
    };

    public static NlpListener NLPListener = new NlpListener() {
        @Override
        public void onResult(SpeechResult speechResult) {
            Log.d(TAG, "NLP onResult " + speechResult.getAnswerText());
            notifyHandler(MSG_UPDATE_NLP_RESULT, speechResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.d(TAG, "NLP onError " + speechError.getErrMsg());
        }
    };

    public static TtsListener TTSListener = new TtsListener() {
        @Override
        public void onTtsTransStart() {
            Log.d(TAG, "TTS onTtsTransStart");
        }

        @Override
        public void onTtsTransEnd(boolean b) {
            Log.d(TAG, "TTS onTtsTransEnd");
        }

        @Override
        public void onPlayStart(AudioTrack audioTrack) {
            Log.d(TAG, "TTS onPlayStart");
        }

        @Override
        public void onPlayFinish() {
            Log.d(TAG, "TTS onPlayFinish");
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.d(TAG, "TTS onError " + speechError.getErrMsg());
            notifyHandler(MSG_UPDATE_ERROR, speechError);
        }

        @Override
        public void onPCMData(PCMInfo pcmInfo) {
            Log.d(TAG, "TTS onPCMData");
        }

        @Override
        public void onTtsGotURL(String s) {
            Log.d(TAG, "TTS onTtsGotURL");
        }
    };

    public static ServiceEventListener ServiceEventListener = new ServiceEventListener() {
        @Override
        public void onEvent(ServiceEvent serviceEvent) {
            Log.d(TAG, "EVENT onEvent" + serviceEvent.getEventMsg());
        }
    };

    public static InstructionListener InstructionListener = new InstructionListener() {
        @Override
        public void onInstruction(Instruction[] instructions, String[] instructionStrings, SpeechResult speechResult) {
            Log.d(TAG, "onInstruction " + instructions.length);
        }
    };
}
