/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.customskill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;


/*
 * This class is the actual skill. Here you receive the input and have to produce the speech output. 
 */
public class AlexaSkillSpeechlet
implements SpeechletV2
{
	static Logger logger = LoggerFactory.getLogger(AlexaSkillSpeechlet.class);

	public static String userRequest;

	private static int sum;
	private static String question = "";
	private static String correctAnswer = "";
	private static enum RecognitionState {Answer, YesNo, OneTwo, VokabelQuiz};
	private RecognitionState recState;
	private static enum UserIntent {vokabeln, quiz, einer, zwei, Yes, No, hello, tree, now, maybe, today, Error};
	UserIntent ourUserIntent;

	static String welcomeMsg = "Hallo, herzlich willkommen bei Quizzitch. Ein oder zwei Spieler?";
	static String singleMsg = "Sie sind im Einzelspielermodus. Vokabeln lernen oder quizzen?";
	static String multiMsg = "Sie sind im Mehrspielermodus. Wenn Sie die Antwort auf die Frage kennen, rufen Sie Ihren Namen. Ist die Antwort korrekt, erhalten Sie Punkte. Los geeeehts!";
	static String difficultyMsg = "Schwierigkeit einfach, mittel oder schwer?";
	static String singleQuizMsg = "Sie sind im Einzelquiz. Los geehts!";
	static String wrongMsg = "Das ist leider falsch.";
	static String correctMsg = "Das ist richtig.";
	static String continueMsg = "Möchten Sie weiterspielen?";
	static String congratsMsg = "Herzlichen Glückwunsch! Sie haben eine Million Punkte gewonnen.";
	static String goodbyeMsg = "Auf Wiedersehen!";
	static String sumMsg = "Sie haben {replacement} Punkte.";
	static String errorYesNoMsg = "Das habe ich nicht verstanden. Sagen Sie bitte ja oder nein.";
	static String errorAnswerMsg = "Das habe ich nicht verstanden. Sagen Sie bitte erneut Ihre Antwort.";
	static String errorOneTwoMsg = "Das habe ich nicht verstanden. Sagen Sie bitte einer oder zwei.";
	static String errorVokabelQuizMsg = "Das habe ich nicht verstanden. Sagen Sie bitte Vokabeln oder Quiz.";


	private String buildString(String msg, String replacement1, String replacement2) {
		return msg.replace("{replacement}", replacement1).replace("{replacement2}", replacement2);
	}





	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope)
	{
		logger.info("Alexa session begins");
		sum = 0;
		recState = RecognitionState.OneTwo;
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope)
	{

		return askUserResponse(welcomeMsg);
		
	}



	private void selectQuestionVokEinfach() {
		switch(sum){
		case 0: question = "Hallo bedeutet auf englisch hello. Sage hallo auf englisch."; correctAnswer = "hello"; break;
		case 50: question = "baum bedeutet auf englisch tree. Sage baum auf englisch."; correctAnswer = "tree"; break;
		case 100: question = "jetzt bedeutet auf englisch now. Sage jetzt auf englisch."; correctAnswer = "now"; break;
		case 200: question = "vielleicht bedeutet auf englisch maybe. Sage vielleicht auf englisch."; correctAnswer = "maybe"; break;
		case 300: question = "heute bedeutet auf englisch today. Sage heute auf englisch."; correctAnswer = "today"; break;
		case 500: question = "das Essen bedeutet auf englisch food.Sage Essen auf englisch"; correctAnswer = "food"; break;
		case 1000: question = "Kopf bedeutet auf englisch head.Sage Kopf auf englisch."; correctAnswer = "head"; break;
		case 2000: question = "Hand bedeutet auf englisch hand.Sage Hand auf englisch"; correctAnswer = "hand"; break;
		case 4000: question = "Haare bedeutet auf englisch Hair.Sage Haare auf englisch."; correctAnswer = "hair"; break;
		case 8000: question = "Bein bedeutet auf englisch leg. Sage Bein auf englisch."; correctAnswer = "leg"; break;
		case 16000: question = "Sonne bedeutet auf englisch sun. Sage Sonne auf englisch."; correctAnswer = "sun"; break;
		case 32000: question = "Immer bedeutet auf englisch always.Sage immer auf englisch"; correctAnswer = "always"; break;
		case 64000: question = "Wasser bedeutet auf englisch water.Sage Wasser auf englisch"; correctAnswer = "water"; break;
		case 125000: question = "Tisch bedeutet auf englisch table. Sage Tisch auf englisch."; correctAnswer = "table"; break;
		case 500000: question = "Stadt bedeutet auf englisch city. Sage Stadt auf englisch."; correctAnswer = "city"; break;
		}
	}

	private void selectQuestionVokMittel() {
		switch(sum){
		case 0: question = "Treppe bedeutet auf englisch stairs. Sage auf Treppe auf englisch."; correctAnswer = "stairs"; break;
		case 50: question = "Haarfarbe bedeutet auf englisch Haircolour.Sage Haarfarbe auf englisch."; correctAnswer = "haircolour""; break;
		case 100: question = "Reifen bedeutet auf englisch wheel. Sage Reifen auf englisch."; correctAnswer = "wheel"; break;
		case 200: question = "Bauchnabel bedeutet auf englisch bellybutton. Sage Bauchnabel auf englisch."; correctAnswer = "bellybutton"; break;
		case 300: question = "gebrochen hei�t auf englisch broken. Sage gebrochen auf englisch."; correctAnswer = "broken"; break;
		case 500: question = "Kerze hei�t auf englisch candle.Sage Kerze auf englisch"; correctAnswer = "candle"; break;
		case 1000: question = "Vertrag hei�t auf englisch contract. Sage Vertrag auf englisch"; correctAnswer = "contract"; break;
		case 2000: question = "Gemeinschaft hei�t auf Englisch community. Sage Gemeinschaft auf englisch."; correctAnswer = "community"; break;
		case 4000: question = "Feld hei�t auf englisch field. Sage Feld auf englisch."; correctAnswer = "field"; break;
		case 8000: question = "Sturm hei�t auf englisch gale. Sage Sturm auf englisch."; correctAnswer = "gale"; break;
		case 16000: question = "aufgeben hei�t auf englisch give up. Sage aufgeben auf englisch"; correctAnswer = "give up"; break;
		case 32000: question = "Mikrowelle hei�t auf englisch microwave. Sage Mikrowelle auf englisch"; correctAnswer = "microwave"; break;
		case 64000: question = "Kopfkissen hei�t auf englisch pillow. Sage Kissen auf englisch."; correctAnswer = "pillow"; break;
		case 125000: question = "Politik hei�t auf englisch policy. Sage Politik auf englisch."; correctAnswer = "Policy"; break;
		case 500000: question = "Gleichgewicht hei�t auf englisch balance. Sage Gleichgewicht auf englisch."; correctAnswer = "Balance"; break;
		}
	}

	private void selectQuestionVokSchwer() {
		switch(sum){
		case 0: question = "Bekannter hei�t auf englisch acquaintance. Sage Bekannter auf englisch."; correctAnswer = "acquaintance"; break;
		case 50: question = "rechthaberisch hei�t auf englisch bossy. Sage rechthaberisch auf englisch."; correctAnswer = "bossy"; break;
		case 100: question = "zuversichtlich bedeutet auf englisch confident. Sage zuversichtlich auf englisch."; correctAnswer = "confident"; break;
		case 200: question = "gro�herzig bedeutet auf englisch generous. Sage gro�herzig auf englisch."; correctAnswer = "generous"; break;
		case 300: question = "Mittelschicht bedeutet auf englisch middle class. Sage Mittelschicht auf englisch."; correctAnswer = "middle class"; break;
		case 500: question = "Schwiegermutter hei�t auf englisch mother in law. Sage Schwiegermutter auf englisch."; correctAnswer = "mother in law"; break;
		case 1000: question = "launisch hei�t auf englisch moody. Sage launisch auf englisch."; correctAnswer = "moody"; break;
		case 2000: question = "vertrauensw�rdig hei�t auf englisch reliable. Sage vetrauensw�rdig auf englisch."; correctAnswer = "reliable"; break;
		case 4000: question = "Rechnungswesen hei�t auf englisch accountancy. Sage auf englisch Rechnungswesen."; correctAnswer = "accountancy"; break;
		case 8000: question = "sich bewerben hei�t auf englisch apply. Sage sich bewerben auf englisch."; correctAnswer = "apply"; break;
		case 16000: question = "flie�end hei�t auf englisch fluently. Sage flie�end auf englisch."; correctAnswer = "fluently"; break;
		case 32000: question = "auf entwas bestehen hei�t auf englisch insist. Sage auf entwas bestehen auf englisch"; correctAnswer = "insist"; break;
		case 64000: question = "Vertreter hei�t auf englisch representative. Sage Vertreter auf englisch."; correctAnswer = "representative"; break;
		case 125000: question = "reibungslos hei�t auf englisch smoothly. Sage reibungslos auf englisch."; correctAnswer = "smoothly"; break;
		case 500000: question = "bereit sein hei�t auf englisch be willing to. Sage bereit sein auf englisch"; correctAnswer = "be willing to"; break;
		}
	}

	private void selectQuestionQuizeinfach() {
		switch(sum){
		case 0: question = "Was bedeutet hallo auf englisch?"; correctAnswer = "hello"; break;
		case 50: question = "Was bedeutet Baum auf englisch?"; correctAnswer = "tree"; break;
		case 100: question = "Was bedeutet jetzt auf englisch?"; correctAnswer = "now"; break;
		case 200: question = "Was bedeutet vielleicht auf englisch?"; correctAnswer = "maybe"; break;
		case 300: question = "Was bedeutet heute auf englisch?"; correctAnswer = "today"; break;
		case 500: question = "Was bedeutet Hand auf englisch?"; correctAnswer = "hand" ; break;
		case 1000: question = "Was bedeutet Haare auf englisch?"; correctAnswer = "hair"; break;
		case 2000: question = "Was bedeutet Bein auf englisch?"; correctAnswer = "leg"; break;
		case 4000: question = "Frage?"; correctAnswer = "today"; break;
		case 8000: question = "Frage?"; correctAnswer = "today"; break;
		case 16000: question = "Frage?"; correctAnswer = "today"; break;
		case 32000: question = "Frage?"; correctAnswer = "today"; break;
		case 64000: question = "Frage?"; correctAnswer = "today"; break;
		case 125000: question = "Frage?"; correctAnswer = "today"; break;
		case 500000: question = "Frage?"; correctAnswer = "today"; break;
		}
	}

	private void selectQuestionQuizmittel() {
		switch(sum){
		case 0: question = "Was bedeutet Reifen auf englisch?"; correctAnswer = "wheel"; break;
		case 50: question = "Was bedeutet Bauchnabel auf englisch?"; correctAnswer = "bellybutton"; break;
		case 100: question = "Was bedeutet Treppe auf englisch?"; correctAnswer = "stairs"; break;
		case 200: question = "Was bedeutet Feld auf englisch?"; correctAnswer = "field"; break;
		case 300: question = "Was hei�t gebrochen auf englisch?"; correctAnswer = "broken"; break;
		case 500: question = "Was hei�t Mikrowelle auf englisch?"; correctAnswer = "microwave"; break;
		case 1000: question = "Was hei�t aufgeben auf englisch?"; correctAnswer = "give up"; break;
		case 2000: question = "Was hei�t Gemeinschaft auf englisch?"; correctAnswer = "community"; break;
		case 4000: question = "Was hei�t Sturm auf englisch?"; correctAnswer = "gale"; break;
		case 8000: question = "Frage?"; correctAnswer = "today"; break;
		case 16000: question = "Frage?"; correctAnswer = "today"; break;
		case 32000: question = "Frage?"; correctAnswer = "today"; break;
		case 64000: question = "Frage?"; correctAnswer = "today"; break;
		case 125000: question = "Frage?"; correctAnswer = "today"; break;
		case 500000: question = "Frage?"; correctAnswer = "today"; break;
		}
	}
	private void selectQuestionQuizschwer() {
		switch(sum){
		case 0: question = "Was bedeutet rechthaberisch auf englisch?"; correctAnswer = "bossy"; break;
		case 50: question = "Was bedeutet Vertreter auf englisch?"; correctAnswer = "representative"; break;
		case 100: question = "Was bedeutet flie�end auf englisch?"; correctAnswer = "fluently"; break;
		case 200: question = "Was bedeutet launisch auf englisch?"; correctAnswer = "moody"; break;
		case 300: question = "Was bedeutet Schwiegermutter auf englisch?"; correctAnswer = "mother in law"; break;
		case 500: question = "Was bedeutet Mittelschicht auf englisch?"; correctAnswer = "middle class"; break;
		case 1000: question = "Frage?"; correctAnswer = "today"; break;
		case 2000: question = "Frage?"; correctAnswer = "today"; break;
		case 4000: question = "Frage?"; correctAnswer = "today"; break;
		case 8000: question = "Frage?"; correctAnswer = "today"; break;
		case 16000: question = "Frage?"; correctAnswer = "today"; break;
		case 32000: question = "Frage?"; correctAnswer = "today"; break;
		case 64000: question = "Frage?"; correctAnswer = "today"; break;
		case 125000: question = "Frage?"; correctAnswer = "today"; break;
		case 500000: question = "Frage?"; correctAnswer = "today"; break;
		}
	}

	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope)
	{
		IntentRequest request = requestEnvelope.getRequest();
		Intent intent = request.getIntent();
		userRequest = intent.getSlot("anything").getValue();
		logger.info("Received following text: [" + userRequest + "]");
		logger.info("recState is [" + recState + "]");
		SpeechletResponse resp = null;
		switch (recState) {
		case Answer: resp = evaluateAnswer(userRequest); break;
		case OneTwo: resp = evaluateOneTwo(userRequest); break;
		case VokabelQuiz: resp = evaluateVokabelQuiz(userRequest); break;
		case YesNo: resp = evaluateYesNo(userRequest); 
		recState = RecognitionState.Answer; break;
		default: resp = response("Erkannter Text: " + userRequest);
		}   
		return resp;
	}

	private SpeechletResponse evaluateYesNo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case Yes: {
			selectQuestionVokEinfach();
			res = askUserResponse(question); break;
		} case No: {
			res = response(buildString(sumMsg, String.valueOf(sum), "")+" "+goodbyeMsg); break;
		} default: {
			res = askUserResponse(errorYesNoMsg);
		}
		}
		return res;
	}
	
	private SpeechletResponse evaluateOneTwo(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case einer: {
			res = askUserResponse(singleMsg);
			recState = RecognitionState.VokabelQuiz; break;
		} case zwei: {
			res = askUserResponse(multiMsg);
			recState = RecognitionState.Answer; break;
		} default: {
			res = askUserResponse(errorOneTwoMsg);
		}
		}
		return res;
	}
	
	private SpeechletResponse evaluateVokabelQuiz(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		case vokabeln: {
			res = askUserResponse(difficultyMsg); break;
		} case quiz: {
			res = askUserResponse(singleQuizMsg); break;
		} default: {
			res = askUserResponse(errorVokabelQuizMsg);
		}
		}
		return res;
	}


	private SpeechletResponse evaluateAnswer(String userRequest) {
		SpeechletResponse res = null;
		recognizeUserIntent(userRequest);
		switch (ourUserIntent) {
		default :{ 
			if (ourUserIntent.equals(UserIntent.hello)
					|| ourUserIntent.equals(UserIntent.tree)
					|| ourUserIntent.equals(UserIntent.now)
					|| ourUserIntent.equals(UserIntent.maybe)
					|| ourUserIntent.equals(UserIntent.today)
					) {
				logger.info("User answer ="+ ourUserIntent.name().toLowerCase()+ "/correct answer="+correctAnswer);
				if (ourUserIntent.name().toLowerCase().equals(correctAnswer)) {
					logger.info("User answer recognized as correct.");
					increaseSum();
					if (sum == 1000000) {
						res = response(correctMsg+" "+congratsMsg+" "+goodbyeMsg);
					} else {
						recState = RecognitionState.YesNo;
						res = askUserResponse(correctMsg+" "+continueMsg);
					}
				} else {
					setfinalSum();
					res = response(wrongMsg+ " "+ sumMsg + " " +goodbyeMsg);
				}
			} else {
				res = askUserResponse(errorAnswerMsg);
			}
		}
		}
		return res;
	}

	private void setfinalSum() {
		if (sum <500){
			sum = 0;
		}else{
			if(sum <16000){
				sum = 500;
			}else{
				sum=16000;
			}
		}

	}

	private void increaseSum() {
		switch(sum){
		case 0: sum = 50; break;
		case 50: sum = 100; break;
		case 100: sum = 200; break;
		case 200: sum = 300; break;
		case 300: sum = 500; break;
		case 500: sum = 1000; break;
		case 1000: sum = 2000; break;
		case 2000: sum = 4000; break;
		case 4000: sum = 8000; break;
		case 8000: sum = 16000; break;
		case 16000: sum = 32000; break;
		case 32000: sum = 64000; break;
		case 64000: sum = 125000; break;
		case 125000: sum = 500000; break;
		case 500000: sum = 1000000; break;
		}
	}

	
	 void recognizeUserIntent(String userRequest) {
		userRequest = userRequest.toLowerCase();
		String pattern4 = "\\bnow\\b";
		String pattern5 = "\\btree\\b";
		String pattern6 = "\\bhello\\b";
		String pattern7 = "\\byes\\b";
		String pattern8 = "\\bno\\b";
		String pattern9 = "\\bmaybe\\b";
		String pattern10 = "\\btoday\\b";
		String pattern11 = "\\beiner\\b";
		String pattern12 = "\\bzwei\\b";
		String pattern13 = "\\bvokabeln\\b";
		String pattern14 = "\\bquiz\\b";
		String pattern15 = "\\bfood\\b";
		String pattern16 = "\\bhead\\b";
		String pattern17 = "\\bhair\\b";
		String pattern18 = "\\bleg\\b";
		String pattern19 = "\\bsun\\b";
		String pattern20 = "\\balways\\b";
		String pattern21 = "\\bwater\\b";
		String pattern22 = "\\btable\\b";
		String pattern23 = "\\bcity\\b";
		String pattern24 = "\\bstairs\\b";
		String pattern25 = "\\bhaircolour\\b";
		String pattern26 = "\\bwheel\\b";
		String pattern27 = "\\bbellybutton\\b";
		String pattern28 = "\\bbroken\\b";
		String pattern29 = "\\bcontract\\b";
		String pattern30 = "\\bcommunity\\b";
		String pattern31 = "\\bcandle\\b";
		String pattern32 = "\\bfield\\b";
		String pattern33 = "\\bgale\\b";
		String pattern34 = "\\bgive up\\b";
		String pattern35 = "\\bmicrowave\\b";
		String pattern36 = "\\bpillow\\b";
		String pattern37 = "\\bpolicy\\b";
		String pattern38 = "\\bbalance\\b";
		String pattern39 = "\\bacquaintance\\b";
		String pattern40 = "\\bbossy\\b";
		String pattern41 = "\\bconfident\\b";
		String pattern42 = "\\bgenerous\\b";
		String pattern43 = "\\bmiddle class\\b";
		String pattern44 = "\\bmother in law\\b";
		String pattern45 = "\\bmoody\\b";
		String pattern46 = "\\breliable\\b";
		String pattern47 = "\\baccountancy\\b";
		String pattern48 = "\\bapply\\b";
		String pattern49 = "\\bfluently\\b";
		String pattern50 = "\\binsist\\b";
		String pattern51 = "\\brepresentative\\b";
		String pattern52 = "\\bsmoothly\\b";
		String pattern53 = "\\bbe willing to\\b";

		Pattern p4 = Pattern.compile(pattern4);
		Matcher m4 = p4.matcher(userRequest);
		Pattern p5 = Pattern.compile(pattern5);
		Matcher m5 = p5.matcher(userRequest);
		Pattern p6 = Pattern.compile(pattern6);
		Matcher m6 = p6.matcher(userRequest);
		Pattern p7 = Pattern.compile(pattern7);
		Matcher m7 = p7.matcher(userRequest);
		Pattern p8 = Pattern.compile(pattern8);
		Matcher m8 = p8.matcher(userRequest);
		Pattern p9 = Pattern.compile(pattern9);
		Matcher m9 = p9.matcher(userRequest);
		Pattern p10 = Pattern.compile(pattern10);
		Matcher m10= p10.matcher(userRequest);
		Pattern p11 = Pattern.compile(pattern11);
		Matcher m11= p11.matcher(userRequest);
		Pattern p12 = Pattern.compile(pattern12);
		Matcher m12= p12.matcher(userRequest);
		Pattern p13 = Pattern.compile(pattern13);
		Matcher m13= p13.matcher(userRequest);
		Pattern p14 = Pattern.compile(pattern14);
		Matcher m14= p14.matcher(userRequest);
		Pattern p15 = Pattern.compile(pattern15);
		Matcher m15 = p15.matcher(userRequest);
		Pattern p16 = Pattern.compile(pattern16);
		Matcher m16 = p16.matcher(userRequest);
		Pattern p17 = Pattern.compile(pattern17);
		Matcher m17 = p17.matcher(userRequest);
		Pattern p18 = Pattern.compile(pattern18);
		Matcher m18 = p18.matcher(userRequest);
		Pattern p19 = Pattern.compile(pattern19);
		Matcher m19 = p19.matcher(userRequest);
		Pattern p20 = Pattern.compile(pattern20);
		Matcher m20 = p20.matcher(userRequest);
		Pattern p21 = Pattern.compile(pattern21);
		Matcher m21= p21.matcher(userRequest);
		Pattern p22 = Pattern.compile(pattern22);
		Matcher m22= p22.matcher(userRequest);
		Pattern p23 = Pattern.compile(pattern23);
		Matcher m23= p23.matcher(userRequest);
		Pattern p24 = Pattern.compile(pattern24);
		Matcher m24= p24.matcher(userRequest);
		Pattern p25 = Pattern.compile(pattern25);
		Matcher m25= p25.matcher(userRequest);
		Pattern p26 = Pattern.compile(pattern26);
		Matcher m26 = p26.matcher(userRequest);
		Pattern p27 = Pattern.compile(pattern27);
		Matcher m27 = p27.matcher(userRequest);
		Pattern p28 = Pattern.compile(pattern28);
		Matcher m28 = p28.matcher(userRequest);
		Pattern p29 = Pattern.compile(pattern29);
		Matcher m29 = p29.matcher(userRequest);
		Pattern p30 = Pattern.compile(pattern30);
		Matcher m30 = p30.matcher(userRequest);
		Pattern p31 = Pattern.compile(pattern31);
		Matcher m31 = p31.matcher(userRequest);
		Pattern p32 = Pattern.compile(pattern32);
		Matcher m32= p.matcher(userRequest);
		Pattern p33 = Pattern.compile(pattern33);
		Matcher m33= p33.matcher(userRequest);
		Pattern p34 = Pattern.compile(pattern43);
		Matcher m34= p34.matcher(userRequest);
		Pattern p35 = Pattern.compile(pattern35);
		Matcher m35= p35.matcher(userRequest);
		Pattern p36 = Pattern.compile(pattern36);
		Matcher m36= p36.matcher(userRequest);
		Pattern p37 = Pattern.compile(pattern37);
		Matcher m37 = p37.matcher(userRequest);
		Pattern p38 = Pattern.compile(pattern38);
		Matcher m38 = p38.matcher(userRequest);
		Pattern p39 = Pattern.compile(pattern39);
		Matcher m39 = p39.matcher(userRequest);
		Pattern p40 = Pattern.compile(pattern40);
		Matcher m40 = p40.matcher(userRequest);
		Pattern p41 = Pattern.compile(pattern41);
		Matcher m41 = p41.matcher(userRequest);
		Pattern p42 = Pattern.compile(pattern42);
		Matcher m42 = p42.matcher(userRequest);
		Pattern p43 = Pattern.compile(pattern43);
		Matcher m43= p43.matcher(userRequest);
		Pattern p44 = Pattern.compile(pattern44);
		Matcher m44= p44.matcher(userRequest);
		Pattern p45 = Pattern.compile(pattern45);
		Matcher m45= p45.matcher(userRequest);
		Pattern p46 = Pattern.compile(pattern46);
		Matcher m46= p46.matcher(userRequest);
		Pattern p47 = Pattern.compile(pattern47);
		Matcher m47= p47.matcher(userRequest);
		Pattern p48 = Pattern.compile(pattern48);
		Matcher m48 = p48.matcher(userRequest);
		Pattern p49 = Pattern.compile(pattern49);
		Matcher m49 = p49.matcher(userRequest);
		Pattern p50 = Pattern.compile(pattern50);
		Matcher m50 = p50.matcher(userRequest);
		Pattern p51 = Pattern.compile(pattern51);
		Matcher m51 = p51.matcher(userRequest);
		Pattern p52 = Pattern.compile(pattern52);
		Matcher m52 = p52.matcher(userRequest);
		Pattern p53 = Pattern.compile(pattern53);
		Matcher m53 = p53.matcher(userRequest);
		
		if (m4.find()) {
			ourUserIntent = UserIntent.now;
		} else if (m5.find()) {
			ourUserIntent = UserIntent.tree;
		} else if (m6.find()) {
			ourUserIntent = UserIntent.hello;
		} else if (m7.find()) {
			ourUserIntent = UserIntent.Yes;
		} else if (m8.find()) {
			ourUserIntent = UserIntent.No;
		} else if (m9.find()) {
			ourUserIntent = UserIntent.maybe;
		} else if (m10.find()) {
			ourUserIntent = UserIntent.today;
		} else if (m11.find()) {
			ourUserIntent = UserIntent.einer;
		} else if (m12.find()) {
			ourUserIntent = UserIntent.zwei;
		} else if (m13.find()) {
			ourUserIntent = UserIntent.vokabeln;
		} else if (m14.find()) {
			ourUserIntent = UserIntent.quiz;
		} else {
			ourUserIntent = UserIntent.Error;
		}
		logger.info("set ourUserIntent to " +ourUserIntent);
	}

	//TODO
	/*private void useFiftyFiftyJoker() {
		answerOption1 = correctAnswer;
		answerOption2 = correctAnswer;
	}

	//TODO
	private void usePublikumJoker() {
		answerOption1 = correctAnswer;
	}*/

	/**
	 * formats the text in weird ways
	 * @param text
	 * @param i
	 * @return
	 */
	private SpeechletResponse responseWithFlavour(String text, int i) {

		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		switch(i){ 
		case 0: 
			speech.setSsml("<speak><amazon:effect name=\"whispered\">" + text + "</amazon:effect></speak>");
			break; 
		case 1: 
			speech.setSsml("<speak><emphasis level=\"strong\">" + text + "</emphasis></speak>");
			break; 
		case 2: 
			String half1=text.split(" ")[0];
			String[] rest = Arrays.copyOfRange(text.split(" "), 1, text.split(" ").length);
			speech.setSsml("<speak>"+half1+"<break time=\"3s\"/>"+ StringUtils.join(rest," ") + "</speak>");
			break; 
		case 3: 
			String firstNoun="erstes Wort buchstabiert";
			String firstN=text.split(" ")[3];
			speech.setSsml("<speak>"+firstNoun+ "<say-as interpret-as=\"spell-out\">"+firstN+"</say-as>"+"</speak>");
			break; 
		case 4: 
			speech.setSsml("<speak><audio src='soundbank://soundlibrary/transportation/amzn_sfx_airplane_takeoff_whoosh_01'/></speak>");
			break;
		default: 
			speech.setSsml("<speak><amazon:effect name=\"whispered\">" + text + "</amazon:effect></speak>");
		} 

		return SpeechletResponse.newTellResponse(speech);
	}


	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope)
	{
		logger.info("Alexa session ends now");
	}



	/**
	 * Tell the user something - the Alexa session ends after a 'tell'
	 */
	private SpeechletResponse response(String text)
	{
		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(text);

		return SpeechletResponse.newTellResponse(speech);
	}

	/**
	 * A response to the original input - the session stays alive after an ask request was send.
	 *  have a look on https://developer.amazon.com/de/docs/custom-skills/speech-synthesis-markup-language-ssml-reference.html
	 * @param text
	 * @return
	 */
	private SpeechletResponse askUserResponse(String text)
	{
		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		speech.setSsml("<speak>" + text + "</speak>");

		// reprompt after 8 seconds
		SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
		repromptSpeech.setSsml("<speak><emphasis level=\"strong\">Hey!</emphasis> Bist du noch da?</speak>");

		Reprompt rep = new Reprompt();
		rep.setOutputSpeech(repromptSpeech);

		return SpeechletResponse.newAskResponse(speech, rep);
	}


}
