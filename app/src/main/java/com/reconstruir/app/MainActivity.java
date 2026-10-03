package com.reconstruir.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
    private LinearLayout root;
    private SharedPreferences prefs;
    private final String[] tabs = {"Hoje","Plano","Hábitos","Reflexão","Evolução","IA"};

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("reconstruir", MODE_PRIVATE);
        showHome();
    }

    private TextView tv(String text, float size, int color) {
        TextView v = new TextView(this);
        v.setText(text); v.setTextSize(size); v.setTextColor(color);
        v.setPadding(0,6,0,6);
        return v;
    }

    private Button btn(String text) {
        Button b = new Button(this);
        b.setText(text); b.setTextColor(Color.WHITE); b.setBackgroundColor(PRIMARY);
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
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG);
        ScrollView scroll = new ScrollView(this); scroll.addView(root);
        LinearLayout shell = new LinearLayout(this); shell.setOrientation(LinearLayout.VERTICAL); shell.setBackgroundColor(BG);
        TextView head = tv("R  "+title,22,INK); head.setPadding(18,24,18,12);
        shell.addView(head); shell.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav = new LinearLayout(this); nav.setGravity(Gravity.CENTER); nav.setBackgroundColor(CARD);
        for(String t:tabs){ Button b=btn(t); b.setTextColor(t.equals(title)?PRIMARY:MUTED); b.setTextSize(10); b.setBackgroundColor(Color.TRANSPARENT); b.setPadding(1,8,1,8);
            b.setOnClickListener(v -> { if(t.equals("Hoje")) showHome(); else if(t.equals("Plano")) showPlan(); else if(t.equals("Hábitos")) showHabits(); else if(t.equals("Reflexão")) showReflection(); else if(t.equals("Evolução")) showEvolution(); else showAI(); });
            nav.addView(b,new LinearLayout.LayoutParams(0,-2,1));
        }
        shell.addView(nav); setContentView(shell);
    }

    private void showHome() {
        if(!prefs.getBoolean("onboard",false)){ showOnboard(); return; }
        base("Hoje");
        LinearLayout h=card();
        h.addView(tv(new SimpleDateFormat("EEEE, dd MMMM", Locale.forLanguageTag("pt-BR")).format(new Date()),13,PRIMARY));
        h.addView(tv("Um passo de cada vez. Uma vida de cada vez.",27,INK));
        h.addView(tv("Olá, "+prefs.getString("name","")+" . Fase "+phase()+".",16,MUTED));
        Button check=btn("Fazer check-in"); check.setOnClickListener(v->showCheckin(h));
        h.addView(check); root.addView(h);
        addTaskCard(1,"Cuidar do essencial de hoje","Escolha uma ação pequena e controlável.",10);
        addTaskCard(2,"Avançar uma pendência importante","Reduza a tarefa até ela caber no momento.",15);
        addTaskCard(3,"Fazer algo que fortaleça sua rotina","Pouco, mas feito, vale mais que um plano impossível.",15);
    }

    private void showOnboard(){
        LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.VERTICAL); r.setPadding(28,80,28,28); r.setBackgroundColor(BG);
        LinearLayout c=card();
        c.addView(tv("R",42,PRIMARY)); c.addView(tv("Reconstruir",30,INK)); c.addView(tv("Você não precisa reconstruir sua vida inteira hoje. Precisa apenas dar o próximo passo certo.",18,MUTED));
        EditText e=new EditText(this); e.setHint("Como podemos chamar você?"); c.addView(e);
        Button b=btn("Começar"); b.setOnClickListener(v->{prefs.edit().putBoolean("onboard",true).putString("name",e.getText().toString().trim()).apply();showHome();}); c.addView(b);
        r.addView(c); setContentView(r);
    }

    private void showCheckin(View parent){
        base("Hoje"); LinearLayout c=card();
        c.addView(tv("Check-in",24,INK));
        addScale(c,"Energia","energy"); addScale(c,"Estresse","stress"); addScale(c,"Sono","sleep"); addScale(c,"Humor","mood");
        Button save=btn("Salvar check-in"); save.setOnClickListener(v->{prefs.edit().putInt("checks",prefs.getInt("checks",0)+1).apply();showHome();}); c.addView(save); root.addView(c,0);
    }

    private void addScale(LinearLayout c,String label,String key){
        c.addView(tv(label+": "+prefs.getInt(key,3)+"/5",15,INK));
        LinearLayout row=new LinearLayout(this);
        for(int i=1;i<=5;i++){Button b=btn(String.valueOf(i)); b.setTextColor(i==prefs.getInt(key,3)?Color.WHITE:PRIMARY); b.setBackgroundColor(i==prefs.getInt(key,3)?PRIMARY:Color.rgb(238,242,240)); final int n=i;
            b.setOnClickListener(v->prefs.edit().putInt(key,n).apply()); row.addView(b,new LinearLayout.LayoutParams(0,-2,1));}
        c.addView(row);
    }

    private void addTaskCard(int id,String title,String why,int minutes){
        LinearLayout c=card(); c.addView(tv(title,19,INK)); c.addView(tv(minutes+" min · "+why,14,MUTED));
        Button b=btn(prefs.getBoolean("task"+id,false)?"Concluída":"Concluir"); b.setOnClickListener(v->{prefs.edit().putBoolean("task"+id,true).apply(); b.setText("Concluída");}); c.addView(b); root.addView(c);
    }

    private String phase(){int e=prefs.getInt("energy",3); return e<=2?"Sobreviver":e==3?"Estabilizar":e==4?"Fortalecer":"Crescer";}

    private void showPlan(){base("Plano"); String[] days={"7 dias","30 dias","90 dias"}; for(String d:days){LinearLayout c=card();c.addView(tv(d,20,INK));c.addView(tv("Poucas prioridades, versões mínimas e revisão do que funcionou.",15,MUTED));root.addView(c);}}

    private void showHabits(){base("Hábitos"); String[][] hs={{"Beber água","6 copos"},{"Caminhar","10 min"},{"Dormir no horário","rotina possível"}}; for(int i=0;i<hs.length;i++){final String k="habit"+i;LinearLayout c=card();c.addView(tv(hs[i][0],19,INK));c.addView(tv("Alvo: "+hs[i][1],14,MUTED));Button b=btn(prefs.getBoolean(k,false)?"Feito":"Registrar hoje");b.setOnClickListener(v->{prefs.edit().putBoolean(k,!prefs.getBoolean(k,false)).apply();b.setText(prefs.getBoolean(k,false)?"Feito":"Registrar hoje");});c.addView(b);root.addView(c);}}

    private void showReflection(){base("Reflexão");LinearLayout c=card();c.addView(tv("Pare. Observe. Ajuste.",26,INK));c.addView(tv("A reflexão procura informação útil para o próximo passo, não culpa.",15,MUTED));EditText e=new EditText(this);e.setHint("O que você aprendeu hoje?");e.setMinLines(5);e.setText(prefs.getString("reflection",""));c.addView(e);Button b=btn("Salvar reflexão");b.setOnClickListener(v->prefs.edit().putString("reflection",e.getText().toString()).apply());c.addView(b);root.addView(c);LinearLayout w=card();w.addView(tv("Revisão semanal",20,INK));w.addView(tv("Funcionou · Falhou · Aprendi · Parar · Continuar · Começar",14,MUTED));root.addView(w);}

    private void showEvolution(){base("Evolução");LinearLayout c=card();int done=0;for(int i=1;i<=3;i++)if(prefs.getBoolean("task"+i,false))done++;int habits=0;for(int i=0;i<3;i++)if(prefs.getBoolean("habit"+i,false))habits++;c.addView(tv("Olhe para o caminho, não só para o resultado de hoje.",25,INK));c.addView(tv("Tarefas concluídas: "+done+"/3",18,PRIMARY));c.addView(tv("Hábitos registrados: "+habits+"/3",18,PRIMARY));c.addView(tv("Check-ins: "+prefs.getInt("checks",0),18,PRIMARY));c.addView(tv("Estado: energia "+prefs.getInt("energy",3)+"/5 · sono "+prefs.getInt("sleep",3)+"/5",15,MUTED));root.addView(c);}

    private void showAI(){base("IA");LinearLayout c=card();c.addView(tv("Reconstruir IA",24,INK));c.addView(tv("Modo local/demonstração. A IA real será conectada pelo backend em uma próxima etapa.",14,MUTED));EditText e=new EditText(this);e.setHint("O que está acontecendo agora?");e.setMinLines(4);c.addView(e);TextView answer=tv("",15,INK);c.addView(answer);Button b=btn("Responder");b.setOnClickListener(v->{String q=e.getText().toString().toLowerCase(Locale.ROOT);String a;if(q.matches(".*(suic|me machucar|não aguento|crise).*"))a="Prioridade: segurança. Procure uma pessoa de confiança e apoio profissional ou serviço de emergência. No Brasil, o CVV atende pelo 188.";else if(q.matches(".*(sobrecar|perdid|não sei).*"))a="Vamos reduzir: escolha uma única coisa e faça apenas os primeiros 5 minutos.";else a="Comece pela menor ação que você controla hoje. Depois, reavalie.";answer.setText(a);});c.addView(b);root.addView(c);}
}
