package com.xiaomi.mibrainsdkdemo;

import com.xiaomi.ai.AsrRequest;
import com.xiaomi.ai.NlpRequest;
import com.xiaomi.ai.SpeechEngine;
import com.xiaomi.ai.TtsRequest;

public class Requests {

    public static AsrRequest getASRRequest() {
        //单独发起ASR请求，即语音转为文字，输入为语音流，返回为识别的文字。
        //包括中间识别结果和最终识别结果。默认为SDK录音模式。
        AsrRequest asrRequest = new AsrRequest();
        asrRequest.enableEncoder(true);//是否开启内置音频编码
        //asrRequest.setEncodeMode(AsrRequest.USE_ENCODER_BV32_FLOAT, 0, 0);//设置为BV32浮点编码
        asrRequest.setEncodeMode(AsrRequest.USE_ENCODER_OPUS,
                AsrRequest.OPUS_FRAMESIZE_1280,
                AsrRequest.OPUS_BITRATES_32K); //若开启内置编码，则需要设定编码模式，推荐设置为OPUS编码
        asrRequest.setUseVad(true); //是否开启VAD
        asrRequest.setVadMode(AsrRequest.VadMode.VAD_MODE_CLOUD); //设置使用云端VAD
        asrRequest.setDataInputMode(AsrRequest.DataInputMode.DATA_INPUT_MODE_RECORDER);//设置使用自带录音模式
        return asrRequest;
    }

    public static AsrRequest getASRDataRequest() {
        //发起ASR请求，由外部传入音频数据
        AsrRequest asrRequest = new AsrRequest();
        asrRequest.enableEncoder(true);//是否开启内置音频编码
        //asrRequest.setEncodeMode(AsrRequest.USE_ENCODER_BV32_FLOAT, 0, 0);//设置为BV32浮点编码
        asrRequest.setEncodeMode(AsrRequest.USE_ENCODER_OPUS,
                AsrRequest.OPUS_FRAMESIZE_1280,
                AsrRequest.OPUS_BITRATES_32K); //若开启内置编码，则需要设定编码模式，推荐设置为OPUS编码
        asrRequest.setUseVad(false); //是否开启VAD
        asrRequest.setDataInputMode(AsrRequest.DataInputMode.DATA_INPUT_MODE_BUFFER);//设置使用外部数据模式
        return asrRequest;
    }

    public static NlpRequest getNLPRequest(String query, boolean newSession) {
        //单独发起NLP请求，输入为文本，返回文本的语义识别信息
        NlpRequest nlpRequest = new NlpRequest();
        nlpRequest.startNewSession(newSession); //是否开始一轮新会话
        nlpRequest.setTextToProcess(query); //设置NLP查询的语句
        return nlpRequest;
    }

    public static TtsRequest getTTSRequest(String query) {
        //单独发起TTS请求，即文字合成为语音，输入为文本，输出为语音流，默认为SDK播放
        TtsRequest ttsRequest = new TtsRequest();
        ttsRequest.setTextToSpeak(query); //设置需要合成的语句
        return ttsRequest;
    }

    public static SpeechEngine.ParamBuilder getCombinedRequest() {
        //设置完整的ASR NLP TTS查询，即一个完整的请求，请求间的关联由服务器处理
        SpeechEngine.ParamBuilder builder = new SpeechEngine.ParamBuilder();
        AsrRequest asrRequest = getASRRequest();
        NlpRequest nlpRequest = new NlpRequest();
        TtsRequest ttsRequest = new TtsRequest();
        builder.needAsr().setAsrRequest(asrRequest)
                .needNlp().setNlpRequest(nlpRequest)
                .needTts().setTtsRequest(ttsRequest);
        return builder;
    }

    public static SpeechEngine.ParamBuilder getCombinedDataRequest() {
        //设置完整的ASR NLP TTS查询，由外部传入音频数据
        SpeechEngine.ParamBuilder builder = new SpeechEngine.ParamBuilder();
        AsrRequest asrRequest = getASRDataRequest();
        NlpRequest nlpRequest = new NlpRequest();
        TtsRequest ttsRequest = new TtsRequest();
        builder.needAsr().setAsrRequest(asrRequest)
                .needNlp().setNlpRequest(nlpRequest)
                .needTts().setTtsRequest(ttsRequest);
        return builder;
    }
}
