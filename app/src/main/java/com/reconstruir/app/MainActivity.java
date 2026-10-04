package com.reconstruir.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(247,244,238);
    private static final int CARD = Color.rgb(255,253,249);
    private static final int INK = Color.rgb(30,43,40);
    private static final int MUTED = Color.rgb(102,113,110);
    private static final int PRIMARY = Color.rgb(29,77,69);
    private static final int SOFT = Color.rgb(238,242,240);

    private LinearLayout root;
    private SharedPreferences prefs;
    private final String[] tabs = {"Hoje","Plano","Hábitos","Reflexão","Evolução","IA"};

    private final String[] lifeAreas = {
            "Minha vida interior",
            "Fé e espiritualidade",
            "Corpo, saúde e energia",
            "Trabalho e estudos",
            "Dinheiro e organização financeira",
            "Família de origem",
            "Paternidade / maternidade",
            "Casamento e vida conjugal",
            "Relacionamentos e amizades",
            "Responsabilidades e organização",
            "Caráter, disciplina e domínio próprio",
            "Propósito, serviço e contribuição",
            "Casa e ambiente de vida"
    };

    private final String[] roles = {
            "Ter mais intimidade com Cristo",
            "Ser um pai presente, amoroso e responsável",
            "Ser um bom filho, presente e grato",
            "Ser um irmão/irmã leal e cuidadoso",
            "Preparar-me para ser um esposo fiel, amoroso e presente",
            "Construir relacionamentos saudáveis e respeitosos",
            "Ser alguém em quem as pessoas possam confiar"
    };

    private final String[] qualities = {
            "Buscar uma vida santa",
            "Ser responsável",
            "Ser equilibrado",
            "Ter domínio próprio",
            "Ser compreensivo",
            "Ser um homem/mulher maduro(a)",
            "Ser amoroso",
            "Ser dedicado",
            "Ser esforçado",
            "Ser leal",
            "Ser fiel",
            "Ser bondoso",
            "Ser manso",
            "Ser humilde",
            "Ser simples",
            "Ser verdadeiro",
            "Ser generoso",
            "Ser caridoso",
            "Ser carinhoso",
            "Ser misericordioso",
            "Ser pontual",
            "Ser disciplinado",
            "Ser paciente",
            "Ser prudente",
            "Ser corajoso",
            "Ser perseverante",
            "Ser grato",
            "Ser honesto",
            "Ser justo",
            "Saber ouvir",
            "Saber conversar com respeito",
            "Saber pedir perdão e perdoar",
            "Cumprir a minha palavra",
            "Ser presente",
            "Cuidar bem da minha saúde",
            "Ter uma rotina mais consistente",
            "Ser organizado",
            "Aprender a lidar melhor com dinheiro",
            "Servir com alegria",
            "Viver de acordo com meus valores"
    };

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("reconstruir", MODE_PRIVATE);
        showHome();
    }

    private TextView tv(String text, float size, int color) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(size);
        v.setTextColor(color);
        v.setPadding(0,6,0,6);
        return v;
    }

    private Button btn(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(PRIMARY);
        b.setAllCaps(false);
        return b;
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(22,20,22,20);
        c.setBackgroundColor(CARD);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(12,10,12,10);
        c.setLayoutParams(p);
        return c;
    }

    private void base(String title) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.VERTICAL);
        shell.setBackgroundColor(BG);
        TextView head = tv("R  "+title,22,INK);
        head.setPadding(18,24,18,12);
        shell.addView(head);
        shell.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setBackgroundColor(CARD);
        for(String t:tabs){
            Button b=btn(t);
            b.setTextColor(t.equals(title)?PRIMARY:MUTED);
            b.setTextSize(10);
            b.setBackgroundColor(Color.TRANSPARENT);
            b.setPadding(1,8,1,8);
            b.setOnClickListener(v->{
                if(t.equals("Hoje")) showHome();
                else if(t.equals("Plano")) showPlan();
                else if(t.equals("Hábitos")) showHabits();
                else if(t.equals("Reflexão")) showReflection();
                else if(t.equals("Evolução")) showEvolution();
                else showAI();
            });
            nav.addView(b,new LinearLayout.LayoutParams(0,-2,1));
        }
        shell.addView(nav);
        setContentView(shell);
    }

    private void showHome() {
        if(!prefs.getBoolean("onboard",false) || !prefs.getBoolean("onboard_v2",false)){
            showOnboard();
            return;
        }
        String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if(!todayKey.equals(prefs.getString("guide_day",""))){
            prefs.edit().putString("guide_day",todayKey).putInt("guide_step",0).apply();
        }
        showGuideChat();
    }

    private TextView bubble(String text, boolean assistant) {
        TextView v = tv(text, 17, assistant ? INK : PRIMARY);
        v.setPadding(18,16,18,16);
        v.setBackgroundColor(assistant ? CARD : SOFT);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(14,8,14,8);
        v.setLayoutParams(p);
        return v;
    }

    private void addAssistantMessage(LinearLayout chat, String text) {
        chat.addView(bubble("Reconstruir · " + text, true));
    }

    private void addUserMessage(LinearLayout chat, String text) {
        TextView v=bubble("Você · " + text,false);
        v.setGravity(Gravity.END);
        chat.addView(v);
    }

    private void showGuideChat() {
        base("Hoje");
        int step = prefs.getInt("guide_step",0);
        LinearLayout chat = new LinearLayout(this);
        chat.setOrientation(LinearLayout.VERTICAL);

        addAssistantMessage(chat,
                "Olá, " + prefs.getString("name","") + ". Vamos fazer o dia juntos. Eu vou te dizer apenas o próximo passo. Você não precisa pensar no resto agora.");

        if(step == 0){
            addAssistantMessage(chat,
                    "Antes de começar, como está sua energia hoje? Escolha de 1 a 5. Isso me ajuda a escolher um ritmo possível para você.");
            LinearLayout scale = new LinearLayout(this);
            scale.setPadding(14,4,14,12);
            for(int i=1;i<=5;i++){
                Button b=btn(String.valueOf(i));
                final int n=i;
                b.setOnClickListener(v->{
                    prefs.edit().putInt("energy",n).putInt("checks",prefs.getInt("checks",0)+1).putInt("guide_step",1).apply();
                    showGuideChat();
                });
                scale.addView(b,new LinearLayout.LayoutParams(0,-2,1));
            }
            chat.addView(scale);
            addAssistantMessage(chat,"Se estiver muito difícil escolher, marque 1. Isso não mede seu valor; apenas nos ajuda a escolher um ritmo.");
        } else {
            GuideStep[] steps=guideSteps();
            int completed=Math.min(step,steps.length);
            for(int i=1;i<completed;i++){
                addAssistantMessage(chat,steps[i].title+" — obrigado por seguir comigo.");
                addUserMessage(chat,"Feito.");
            }

            if(step >= steps.length){
                addAssistantMessage(chat,"Terminamos o roteiro essencial de hoje. Você não precisa fazer mais nada agora. Respire, descanse e observe como está.");
                Button restart=btn("Recomeçar o guia");
                restart.setOnClickListener(v->{prefs.edit().putInt("guide_step",0).apply(); showGuideChat();});
                chat.addView(restart);
            } else {
                GuideStep current=steps[step];
                addAssistantMessage(chat,current.prompt);
                addAssistantMessage(chat,"A ação de agora é: " + current.action);

                Button done=btn("Vou fazer agora");
                done.setOnClickListener(v->completeGuideStepConversational(step));
                chat.addView(done);

                Button already=btn("Já fiz");
                already.setTextColor(PRIMARY);
                already.setBackgroundColor(SOFT);
                already.setOnClickListener(v->completeGuideStepConversational(step));
                chat.addView(already);

                if(current.minimum != null){
                    Button stuck=btn("Estou travado");
                    stuck.setTextColor(PRIMARY);
                    stuck.setBackgroundColor(SOFT);
                    stuck.setOnClickListener(v->addAssistantMessage(chat,"Tudo bem. Vamos diminuir: " + current.minimum));
                    chat.addView(stuck);
                }
            }
        }

        ScrollView scroll=new ScrollView(this);
        scroll.addView(chat);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    }

    private void completeGuideStepConversational(int step) {
        prefs.edit().putInt("guide_step",step+1).apply();
        showGuideChat();
    }

    private void showCheckin(View parent){
        base("Hoje");
        LinearLayout c=card();
        c.addView(tv("Check-in",24,INK));
        addScale(c,"Energia","energy");
        addScale(c,"Estresse","stress");
        addScale(c,"Sono","sleep");
        addScale(c,"Humor","mood");
        Button save=btn("Salvar check-in");
        save.setOnClickListener(v->{
            int next = Math.max(1, prefs.getInt("guide_step",0)+1);
            prefs.edit().putInt("checks",prefs.getInt("checks",0)+1).putInt("guide_step",next).apply();
            showHome();
        });
        c.addView(save);
        root.addView(c,0);
    }

    private void addScale(LinearLayout c,String label,String key){
        c.addView(tv(label+": "+prefs.getInt(key,3)+"/5",15,INK));
        LinearLayout row=new LinearLayout(this);
        for(int i=1;i<=5;i++){
            Button b=btn(String.valueOf(i));
            b.setTextColor(i==prefs.getInt(key,3)?Color.WHITE:PRIMARY);
            b.setBackgroundColor(i==prefs.getInt(key,3)?PRIMARY:SOFT);
            final int n=i;
            b.setOnClickListener(v->prefs.edit().putInt(key,n).apply());
            row.addView(b,new LinearLayout.LayoutParams(0,-2,1));
        }
        c.addView(row);
    }

    private void addTaskCard(int id,String title,String why,int minutes){
        LinearLayout c=card();
        c.addView(tv(title,19,INK));
        c.addView(tv(minutes+" min · "+why,14,MUTED));
        Button b=btn(prefs.getBoolean("task"+id,false)?"Concluída":"Concluir");
        b.setOnClickListener(v->{prefs.edit().putBoolean("task"+id,true).apply(); b.setText("Concluída");});
        c.addView(b);
        root.addView(c);
    }

    private String phase(){
        int e=prefs.getInt("energy",3);
        return e<=2?"Sobreviver":e==3?"Estabilizar":e==4?"Fortalecer":"Crescer";
    }

    private void showPlan(){
        base("Plano");
        String[] days={"7 dias","30 dias","90 dias"};
        for(String d:days){
            LinearLayout c=card();
            c.addView(tv(d,20,INK));
            c.addView(tv("Poucas prioridades, versões mínimas e revisão do que funcionou.",15,MUTED));
            root.addView(c);
        }
    }

    private void showHabits(){
        base("Hábitos");
        String[][] hs={{"Beber água","6 copos"},{"Caminhar","10 min"},{"Dormir no horário","rotina possível"}};
        for(int i=0;i<hs.length;i++){
            final String k="habit"+i;
            LinearLayout c=card();
            c.addView(tv(hs[i][0],19,INK));
            c.addView(tv("Alvo: "+hs[i][1],14,MUTED));
            Button b=btn(prefs.getBoolean(k,false)?"Feito":"Registrar hoje");
            b.setOnClickListener(v->{prefs.edit().putBoolean(k,!prefs.getBoolean(k,false)).apply(); b.setText(prefs.getBoolean(k,false)?"Feito":"Registrar hoje");});
            c.addView(b);
            root.addView(c);
        }
    }

    private void showReflection(){
        base("Reflexão");
        LinearLayout c=card();
        c.addView(tv("Pare. Observe. Ajuste.",26,INK));
        c.addView(tv("A reflexão procura informação útil para o próximo passo, não culpa.",15,MUTED));
        EditText e=new EditText(this);
        e.setHint("O que você aprendeu hoje?");
        e.setMinLines(5);
        e.setText(prefs.getString("reflection",""));
        c.addView(e);
        Button b=btn("Salvar reflexão");
        b.setOnClickListener(v->prefs.edit().putString("reflection",e.getText().toString()).apply());
        c.addView(b);
        root.addView(c);
        LinearLayout w=card();
        w.addView(tv("Revisão semanal",20,INK));
        w.addView(tv("Funcionou · Falhou · Aprendi · Parar · Continuar · Começar",14,MUTED));
        root.addView(w);
    }

    private void showEvolution(){
        base("Evolução");
        LinearLayout c=card();
        int done=0;
        for(int i=1;i<=3;i++) if(prefs.getBoolean("task"+i,false)) done++;
        int habits=0;
        for(int i=0;i<3;i++) if(prefs.getBoolean("habit"+i,false)) habits++;
        c.addView(tv("Olhe para o caminho, não só para o resultado de hoje.",25,INK));
        c.addView(tv("Tarefas concluídas: "+done+"/3",18,PRIMARY));
        c.addView(tv("Hábitos registrados: "+habits+"/3",18,PRIMARY));
        c.addView(tv("Check-ins: "+prefs.getInt("checks",0),18,PRIMARY));
        c.addView(tv("Estado: energia "+prefs.getInt("energy",3)+"/5 · sono "+prefs.getInt("sleep",3)+"/5",15,MUTED));
        String areas = prefs.getString("selected_areas","");
        if(!areas.isEmpty()) c.addView(tv("Áreas escolhidas: "+countValues(areas),15,MUTED));
        String q = prefs.getString("selected_qualities","");
        if(!q.isEmpty()) c.addView(tv("Qualidades escolhidas: "+countValues(q),15,MUTED));
        root.addView(c);
    }

    private int countValues(String s){
        if(s==null || s.trim().isEmpty()) return 0;
        return s.split("\\|").length;
    }

    private void showAI(){
        base("IA");
        LinearLayout c=card();
        c.addView(tv("Reconstruir IA",24,INK));
        c.addView(tv("Modo local/demonstração. A IA real será conectada pelo backend em uma próxima etapa.",14,MUTED));
        EditText e=new EditText(this);
        e.setHint("O que está acontecendo agora?");
        e.setMinLines(4);
        c.addView(e);
        TextView answer=tv("",15,INK);
        c.addView(answer);
        Button b=btn("Responder");
        b.setOnClickListener(v->{
            String q=e.getText().toString().toLowerCase(Locale.ROOT);
            String a;
            if(q.matches(".*(suic|me machucar|não aguento|crise).*"))
                a="Prioridade: segurança. Procure uma pessoa de confiança e apoio profissional ou serviço de emergência. No Brasil, o CVV atende pelo 188.";
            else if(q.matches(".*(sobrecar|perdid|não sei).*"))
                a="Vamos reduzir: escolha uma única coisa e faça apenas os primeiros 5 minutos.";
            else
                a="Comece pela menor ação que você controla hoje. Depois, reavalie.";
            answer.setText(a);
        });
        c.addView(b);
        root.addView(c);
    }
}
