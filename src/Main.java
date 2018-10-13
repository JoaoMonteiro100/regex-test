import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static List<Phrase> phrases = new ArrayList<>();
    private static final String numbers = "[0-9]+";
    private static final String timeVarsPT = "(minuto(s)?|hora(s)?|dia(s)?|semana(s)?|mês|meses|antes|depois|após|durante|enquanto)";
    private static final String eventVarsPT = "(refeiç(ão|ões)|pequeno(s)?-almoço(s)?|almoço(s)?|almo(ce|ça|çar)|lanche(s)?|jantar(es)?|jant(e|a|ar)|ceia(s)?|deit(e|ar)|durma|dormir|acord(e|ar)|levant(e|ar)|com(a|er)|comida)";
    private static final String numbersPT = "(" + numbers + "|um(a)?|dois|duas|três|quatro|cinco|seis|sete|oito|nove|dez|onze|doze|treze|catorze|quinze|dezasseis|dezassete|dezoito|dezanove|vinte)";
    private static final String timeVarsEN = "(minute(s)?|hour(s)?|day(s)?|week(s)?|month(s)?|before|after|during|while)";
    private static final String eventVarsEN = "(meal(s)?|breakfast(s)?|lunch(es)?|snack(s)?|dinner(s)?|supper(s)?|din(e|ing)|eat(ing)|(go(ing)? to )?sleep(ing)?|wak(e|ing)( up)?|(go(ing)? to )?bed|food)";
    private static final String numbersEN = "(" + numbers + "|once|one|twice|two|thrice|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty)";

    private static final String anyString = "[a-zA-Z]*";
    private static final String oneLetter = "[a-zA-Z].";
    private static final String coupleWords = "(\\b\\w+\\b){0,2}";

    private static final String measurementsPT = "\\b" + anyString + "(grama(s)?|litro(s)?)\\b|\\b" + oneLetter + "[glL]\\b";
    private static final String verbPrefixesPT = "(tom|receit|injet|beb|consum|apli|administr|inal|espalh|distribu|instil|po|pu|col|ad|us|empreg|dilu|dissolv|eng)";
    private static final String verbsPT = "\\b" + verbPrefixesPT + anyString + "\\b";
    private static final String relatedPT = "\\b(comprimido|medicamento(s)?|capsula|medicacao|dose(s)?|dosage(m|ns)|posologia|bisnaga|bomba|xarope|remedio|efervescente|pilula|tubo|pastilha(s)?|tablete(s)?|gota(s)?|adesivo(s)?|ampola(s)?|pomada|balsamo|creme|saqueta(s)?|penso|ligadura)\\b";
    private static final String negationPT = "(evit[a-z]{0,30}|deix[a-z]{0,30}|par[a-z]{0,30}|interromp[a-z]{0,30}|termin[a-z]{0,30}|suspend[a-z]{0,30}|nao|tampouco|nem|nunca|jamais)";
    private static final String additionPT = "(e|ainda|tambem|adicionalmente)";
    private static final String adversePT = "(contudo|mas|obstante|entanto|porem|todavia)";
    private static final String negativeLookbehindPT = "(?<!(\\b" + adversePT + "\\b |\\b" + negationPT + "\\b ))";

    private static final String measurementsEN = "\\b" + anyString + "(gram(s)?|liter(s)?)\\b|\\b" + oneLetter + "[glL]\\b";
    private static final String verbPrefixesEN = "(tak|took|administrat|inject|spread|distribut|drink|consum|apply|inhal|instill|put|stick|adher|affix|us|employ|dissolv|dilut|swallow|prescribe)";
    private static final String verbsEN = "\\b" + verbPrefixesEN + anyString + "\\b";
    private static final String relatedEN = "(pill|drug|medication|dose|dosage|capsule|effervescent|posology|tube|pipe|syrup|medicine|remedy|bomb|tablet|pellet|wafer|lozenge|pastille|pastil|drops|droplets|adhesive|ampoule|ampule|ointment|balm|creme|pomade|sachets|ounce(s)?|oz|bond|bind|bandage|ligature|prescription)";
    private static final String negationEN = "(avoid[a-z]{0,30}|stop[a-z]{0,30}|interrupt[a-z]{0,30}|suspend[a-z]{0,30}|terminat[a-z]{0,30}|don't|can't|won't|shouldn't|not|no|never)";
    private static final String additionEN = "(and|moreover|also|furthermore|additionally)";
    private static final String adverseEN = "(however|but|although|though|still|regardless|irrespective)";
    private static final String negativeLookbehindEN = "(?<!(\\b" + adverseEN + "\\b |\\b" + negationEN + "\\b ))";

    //how many words around the match do we want to retrieve to create the phrase with potential medication and/or posology
    private static final String beforeMatch = "(\\b\\w+\\b\\s?){0,10}(";
    private static final String afterMatch = ")(\\s?\\b\\w+\\b){0,10}";

    /**
     * Does all the things
     * @param args
     */
    public static void main(String[] args) {
        //this is supposed to be the whole transcription text
        String transcription =
                "O paciente teve um enfarte agudo do miocárdio há uma semana mas mostra uma boa recuperação isto significa que o músculo cardíaco ficou lesionado depois de um ataque cardíaco o miocárdio é o músculo cardíaco o coração é um órgão vai tomar benuron todos os dias depois da refeição durante 2 meses não pode fazer esforços pesados";

        String language = "PT"; //options are "PT" or "EN"
        generatePhrases(language);

        List<String> probableMedications = locateMedication(language, removeDiacriticalMarks(transcription));
        for(String s : probableMedications) {
            matchPhrase(s.toLowerCase());
        }
    }

    /**
     * Tive que fazer isto porque senão o Regex não funcionava... transforma todos os acentos, cedilhas, etc em caracteres normais
     * @param string String a "limpar"
     * @return Stirng "limpa"
     */
    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Through the recognisement of specific keywords, it detects phrases that have the potential of containing medication and/or posology
     * @param lang Language of the transcription text
     * @param transcript Full transcription text
     * @return Array of phrases that may contain medication and/or posology
     */
    private static List<String> locateMedication(String lang, String transcript) {
        List<Pattern> keywords = new ArrayList<>();
        List<String> result = new ArrayList<>();

        switch (lang) {
            case "PT":
                keywords.add(Pattern.compile(beforeMatch + negativeLookbehindPT + coupleWords + verbsPT + "|" + negativeLookbehindPT + coupleWords + relatedPT + "|" + negativeLookbehindPT + coupleWords + measurementsPT + afterMatch));
                break;

            default: //it's "EN" by default
                keywords.add(Pattern.compile(beforeMatch + negativeLookbehindEN + coupleWords + verbsEN + "|" + negativeLookbehindEN + coupleWords + relatedEN + "|" + negativeLookbehindEN + coupleWords + measurementsEN + afterMatch));
                break;
        }

        for (Pattern key : keywords) {
            Matcher m = key.matcher(transcript);
            while (m.find()) {
                System.out.println("| Phrase likely to contain medication: " + m.group(0));
                result.add(m.group(0));
            }
        }

        return result;
    }

    /**
     * Populates the array phrases that contains the Regex patterns to recognise posologies
     * @param lang Language being used
     */
    private static void generatePhrases(String lang) {
        //order of the arguments:
        //PATTERN, NUMBER, TIME UNIT, NUMBER-PER-TIME, EVENT
        switch (lang) {
            case "PT":
                phrases.add(new Phrase(Pattern.compile("^(.*)" + timeVarsPT + " d(e|a|o)(s)? (tod(o|a)(s)? ((o|a)(s)? )?|cada )?" + eventVarsPT + "(.*)"),-1,2,false,15));
                phrases.add(new Phrase(Pattern.compile("^(.*)(a |em )?cada " + numbersPT + " " + timeVarsPT + "(.*)"),3,5,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)(a |em )?cada " + timeVarsPT + "(.*)"),-1,3,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)(a |em |com )?cada " + eventVarsPT + "(.*)"),-1,-1,false,3));
                phrases.add(new Phrase(Pattern.compile("^(.*)de " + numbersPT + " em " + numbersPT + " " + timeVarsPT + "(.*)"),4,6,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)" + numbersPT + "( vez(es)?)?( \\w+ )?(por|ao) " + timeVarsPT + "(.*)"),2,6,true,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)por " + eventVarsPT + "(.*)"),-1,-1,false,2));
                phrases.add(new Phrase(Pattern.compile("^(.*)tod(os|as)( os| as)? " + timeVarsPT + "(.*)"),-1,4,false,-1));
                phrases.add(new Phrase(Pattern.compile("^(.*)tod(os|as)( os| as)? " + eventVarsPT + "(.*)"),-1,-1,false,4));
                phrases.add(new Phrase(Pattern.compile("^(.*)com ((o|a)(s)? )?" + eventVarsPT + "(.*)"),-1,-1,false,5));
                phrases.add(new Phrase(Pattern.compile("^(.*)(quando|sempre que|juntamente com)(.*)" + eventVarsPT + "(.*)"),-1,-1,false,4));
                phrases.add(new Phrase(Pattern.compile("^(.*)jejum|até|sempre que|juntamente com|ao mesmo tempo que(.*)"),-1,-1,false,-1));
                break;

            default: //it's "EN" by default
                phrases.add(new Phrase(Pattern.compile("(.*)every " + numbersEN + " " + timeVarsEN + "(.*)"),2,3,false,-1));
                phrases.add(new Phrase(Pattern.compile("(.*)" + numbersEN + " (times )?(each|every|a(n)?|per) " + timeVarsEN + "(.*)"),2,6,true,-1));
                phrases.add(new Phrase(Pattern.compile("(.*)(every|each) " + timeVarsEN + "(.*)"),-1,2,false,-1));
                phrases.add(new Phrase(Pattern.compile("(.*)" + timeVarsEN + " (each |every |all |a(n)? )?" + eventVarsEN + "(.*)"),-1,2,false,9));
                phrases.add(new Phrase(Pattern.compile("(.*)with (each |every |all |a(n)? )?" + eventVarsEN + "(.*)"),-1,-1,false,3));
                phrases.add(new Phrase(Pattern.compile("(.*)(when|every|together with)(.*)" + eventVarsEN + "(.*)"),-1,-1,false,4));
                phrases.add(new Phrase(Pattern.compile("(.*)fasting|until|every time|whenever|anytime|up to(.*)"),-1,-1,false,-1));
                break;
        }
    }

    /**
     * Given a phrase, it detects any posologies in it. ATM it just only prints them; in the future it should interpret them according to the variables in Class Phrase
     * @param s Phrase that may contain posologies
     */
    private static void matchPhrase(String s) {
        Matcher m;

        for(Phrase p : phrases) {
            m = p.getP().matcher(s);
            while (m.find()) {
                System.out.println(">>> Found a posology: " + m.group(0));
                /*
                System.out.println("(0): " + m.group(0)); // whole matched expression
                System.out.println("(1): " + m.group(1));
                System.out.println("(2): " + m.group(2));
                System.out.println("(3): " + m.group(3));
                System.out.println("(4): " + m.group(4));
                System.out.println("(5): " + m.group(5));
                System.out.println("(6): " + m.group(6));
                System.out.println("(7): " + m.group(7));
                System.out.println("(8): " + m.group(8));
                System.out.println("(9): " + m.group(9));
                System.out.println("(10): " + m.group(10));
                System.out.println("(11): " + m.group(11));
                System.out.println("(12): " + m.group(12));
                System.out.println("(13): " + m.group(13));
                System.out.println("(14): " + m.group(14));
                System.out.println("(15): " + m.group(15));
                */
                break;
            }
        }
    }
}
