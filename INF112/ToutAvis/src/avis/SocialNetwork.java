package avis;

import avis.models.*;
import avis.structures.ItemKey;
import avis.structures.MemberKey;
import exception.*;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author A. Beugnard,
 * @author G. Ouvradou
 * @author B. Prou
 * @date février - mars 2011
 * @version V0.6
 */

/**
 * <p>
 * <b>Système de mutualisation d'opinions portant sur des domaines
 * variés (littérature, cinéma, art, gastronomie, etc.) et non limités.</b>
 * </p>
 * <p>
 * L'accès aux items et aux opinions qui leurs sont associées
 * est public. La création d'item et le dépôt d'opinion nécessite en revanche
 * que l'utilisateur crée son profil au préalable.
 * </p>
 * <p>
 * Lorsqu'une méthode peut lever deux types d'exception, et que les conditions sont réunies
 * pour lever l'une et l'autre, rien ne permet de dire laquelle des deux sera effectivement levée.
 * </p>
 * <p>
 * Dans une version avancée (version 2), une opinion peut également
 * être évaluée. Chaque membre se voit dans cette version décerner un "karma" qui mesure
 * la moyenne des notes portant sur les opinions qu'il a émises.
 * L'impact des opinions entrant dans le calcul de la note moyenne attribuée à un item
 * est pondéré par le karma des membres qui les émettent.
 * </p>
 */

public class SocialNetwork {

    /**
     * L'instance unique de SocialNetwork
     */
    private static SocialNetwork instance = null;

    /**
     * La liste des items associés au SocialNetwork.
     *
     * @uml.property name="items"
     * @uml.associationEnd multiplicity="(0 -1)" aggregation="composite" inverse="socialNetwork:avis.models.Item"
     */
    private final HashMap<ItemKey, Item> items;

    /**
     * La liste des membres associés au SocialNetwork.
     *
     * @uml.property name="members"
     * @uml.associationEnd multiplicity="(0 -1)" aggregation="composite" inverse="socialNetwork:avis.models.Member"
     */
    private final HashMap<MemberKey, Member> members;

    /**
     * Initialise un <i>SocialNetwok</i>.
     */
    public SocialNetwork() {
        this.items = new HashMap<>();
        this.members = new HashMap<>();
    }

    /**
     * Méthode permettant de renvoyer une instance de la classe SocialNetwork
     *
     * @return Retourne l'instance du singleton SocialNetwork.
     */
    public final static SocialNetwork getInstance() {
        if (SocialNetwork.instance == null) {
            synchronized (SocialNetwork.class) { /* Empêche la création d'une instance par plusieurs threads */
                if (SocialNetwork.instance == null) {
                    SocialNetwork.instance = new SocialNetwork();
                }
            }
        }
        return SocialNetwork.instance;
    }

    /**
     * Obtenir le nombre de membres du <i>SocialNetwork</i>.
     *
     * @return le nombre de membres.
     */
    public int nbMembers() {
        return members.size();
    }

    /**
     * Obtenir le nombre de films du <i>SocialNetwork</i>.
     *
     * @return le nombre de films.
     */
    public int nbFilms() {
        return countItems(Film.class);
    }

    /**
     * Obtenir le nombre de livres du <i>SocialNetwork</i>.
     *
     * @return le nombre de livres.
     */
    public int nbBooks() {
        return countItems(Book.class);
    }

    /**
     * Ajouter un nouveau membre au <i>SocialNetwork</i>.
     *
     * @param pseudo   son pseudo.
     * @param password son mot de passe.
     * @param profil   un slogan choisi par le membre pour se définir.
     * @throws BadEntry            <ul>
     *                             <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces .</li>
     *                             <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                             <li>si le profil n'est pas instancié.</li>
     *                             </ul><br>
     * @throws MemberAlreadyExists membre de même pseudo déjà présent dans le <i>SocialNetwork</i> (même pseudo : indifférent à  la casse  et aux leadings et trailings blanks).
     */
    public void addMember(String pseudo, String password, String profil) throws BadEntry, MemberAlreadyExists {
        Member member = new Member(pseudo, password, profil);

        if (members.containsKey(member.mapKey)) {
            throw new MemberAlreadyExists();
        }

        this.members.put(member.mapKey, member);
    }

    /**
     * Ajouter un nouvel item de film au <i>SocialNetwork</i>.
     *
     * @param pseudo      le pseudo du membre.
     * @param password    le password du membre.
     * @param titre       le titre du film.
     * @param genre       son genre (aventure, policier, etc.).
     * @param realisateur le réalisateur.
     * @param scenariste  le scénariste.
     * @param duree       sa durée en minutes.
     * @throws BadEntry              <ul>
     *                               <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                               <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                               <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                               <li>si le genre n'est pas instancié.</li>
     *                               <li>si le réalisateur n'est pas instancié.</li>
     *                               <li>si le scénariste n'est pas instancié.</li>
     *                               <li>si la durée n'est pas positive.</li>
     *                               </ul><br>
     * @throws NotMember             si le pseudo n'est pas celui d'un membre ou si le pseudo et le password ne correspondent pas.
     * @throws ItemFilmAlreadyExists item film de même titre  déjà présent (même titre : indifférent à  la casse  et aux leadings et trailings blanks).
     */
    public void addItemFilm(String pseudo, String password, String titre, String genre, String realisateur, String scenariste, int duree) throws BadEntry, NotMember, ItemFilmAlreadyExists {
        findMatchingMember(pseudo, password);

        Film film = new Film(titre, genre, realisateur, scenariste, duree);

        if (items.containsKey(film.mapKey)) {
            throw new ItemFilmAlreadyExists();
        }

        this.items.put(film.mapKey, film);
    }

    /**
     * Ajouter un nouvel item de livre au <i>SocialNetwork</i>.
     *
     * @param pseudo   le pseudo du membre.
     * @param password le password du membre.
     * @param titre    le titre du livre.
     * @param genre    son genre (roman, essai, etc.).
     * @param auteur   l'auteur.
     * @param nbPages  le nombre de pages.
     * @throws BadEntry              <ul>
     *                               <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces .</li>
     *                               <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                               <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                               <li>si le genre n'est pas instancié.</li>
     *                               <li>si l'auteur n'est pas instancié.</li>
     *                               <li>si le nombre de pages n'est pas positif.</li>
     *                               </ul><br>
     * @throws NotMember             si le pseudo n'est pas celui d'un membre ou si le pseudo et le password ne correspondent pas.
     * @throws ItemBookAlreadyExists item livre de même titre  déjà présent (même titre : indifférent à la casse  et aux leadings et trailings blanks).
     */
    public void addItemBook(String pseudo, String password, String titre, String genre, String auteur, int nbPages) throws BadEntry, NotMember, ItemBookAlreadyExists {
        findMatchingMember(pseudo, password);

        Book book = new Book(titre, genre, auteur, nbPages);

        if (items.containsKey(book.mapKey)) {
            throw new ItemBookAlreadyExists();
        }

        this.items.put(book.mapKey, book);
    }

    /**
     * Consulter les items du <i>SocialNetwork</i> par nom.
     *
     * @param title son titre (eg. titre d'un film, d'un livre, etc.).
     * @return LinkedList<String> la liste des représentations de tous les items ayant ce nom.
     * Cette représentation contiendra la note de l'item s'il a été noté.
     * (une liste vide si aucun item ne correspond).
     * @throws BadEntry : si le nom n'est pas instancié ou a moins de 1 caractère autre que des espaces.
     */
    public LinkedList<String> consultItems(String title) throws BadEntry {
        if (!Item.titleIsValid(title)) {
            throw new BadEntry("Title does not meet the requirements.");
        }

        LinkedList<String> itemsStrings = new LinkedList<>();

        for (Class klass : new Class[]{Book.class, Film.class}) {
            ItemKey hashKey = new ItemKey(klass, title);
            if (items.containsKey(hashKey)) {
                itemsStrings.add(items.get(hashKey).toString());
            }
        }

        return itemsStrings;
    }

    /**
     * Donner son opinion sur un item film.
     * Ajoute l'opinion de ce membre sur ce film au <i>SocialNetwork</i>.
     * Si une opinion de ce membre sur ce film  préexiste, elle est mise à jour avec ces nouvelles valeurs.
     *
     * @param pseudo      pseudo du membre émettant l'opinion.
     * @param password    son mot de passe.
     * @param titre       titre du film  concerné.
     * @param note        la note qu'il donne au film.
     * @param commentaire ses commentaires.
     * @return la note moyenne des notes sur ce film.
     * @throws BadEntry  <ul>
     *                   <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                   <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si la note n'est pas comprise entre 0.0 et 5.0.</li>
     *                   <li>si le commentaire n'est pas instancié.</li>
     *                   </ul><br>
     * @throws NotMember si le pseudo n'est pas celui d'un membre ou si le pseudo et le password ne correspondent pas.
     * @throws NotItem   si le titre n'est pas le titre d'un film.
     */
    public float reviewItemFilm(String pseudo, String password, String titre, float note, String commentaire) throws BadEntry, NotMember, NotItem {
        return reviewItem(Film.class, pseudo, password, titre, note, commentaire);
    }

    /**
     * Donner son opinion sur un item livre.
     * Ajoute l'opinion de ce membre sur ce livre au <i>SocialNetwork</i>.
     * Si une opinion de ce membre sur ce livre  préexiste, elle est mise à jour avec ces nouvelles valeurs.
     *
     * @param pseudo      pseudo du membre émettant l'opinion.
     * @param password    son mot de passe.
     * @param titre       titre du livre  concerné.
     * @param note        la note qu'il donne au livre.
     * @param commentaire ses commentaires.
     * @return la note moyenne des notes sur ce livre.
     * @throws BadEntry  <ul>
     *                   <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                   <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si la note n'est pas comprise entre 0.0 et 5.0.</li>
     *                   <li>si le commentaire n'est pas instancié.</li>
     *                   </ul><br>
     * @throws NotMember si le pseudo n'est pas celui d'un membre ou si le pseudo et le password ne correspondent pas.
     * @throws NotItem   si le titre n'est pas le titre d'un livre.
     */
    public float reviewItemBook(String pseudo, String password, String titre, float note, String commentaire) throws BadEntry, NotMember, NotItem {
        return reviewItem(Book.class, pseudo, password, titre, note, commentaire);
    }

    /**
     * Ajoute une review à un item.
     *
     * @param klass       le type de l'item ({@code Book} ou {@code Film}).
     * @param pseudo      le pseudo du membre émettant l'opinion.
     * @param password    le mot de passe du membre émettant l'opinion.
     * @param titre       le titre de l'item.
     * @param note        la note attribué à l'item par le membre.
     * @param commentaire le commentaire attribué à l'item par le membre.
     * @return la nouvelle note moyenne du l'item.
     * @throws BadEntry  <ul>
     *                   <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                   <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si la note n'est pas comprise entre 0.0 et 5.0.</li>
     *                   <li>si le commentaire n'est pas instancié.</li>
     *                   </ul>
     * @throws NotMember si le pseudo n'est pas celui d'un membre ou si le pseudo et le password ne correspondent pas.
     * @throws NotItem   si le titre n'est pas le titre d'un item du même type.
     */
    private float reviewItem(Class<?> klass, String pseudo, String password, String titre, float note, String commentaire) throws BadEntry, NotMember, NotItem {
        Item item = findMatchingItem(klass, titre);
        Member member = findMatchingMember(pseudo, password);

        // On recherche la précédente review du membre pour cet item.
        // - Si elle existe on la met à jour.
        // - Sinon on en crée une.
        Review review = member.findReview(item);
        if (review == null) {
            review = new Review(item, member, commentaire, note);
            member.addReview(review);
            item.addReview(review);
        } else {
            review.update(commentaire, note);
        }

        return item.getRating();
    }

    /**
     * Note un avis sur un livre.
     *
     * @param pseudo       le pseudo du membre noteur.
     * @param password     le password du membre noteur.
     * @param reviewPseudo le pseudo de l'auteur de l'avis.
     * @param reviewTitle  le titre de l'item.
     * @param grade        la note attribué à l'avis.
     * @return la nouvelle note moyenne associé à l'avis.
     * @throws BadEntry    <ul>
     *                     <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                     <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                     <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                     <li>si la note n'est pas comprise entre 1.0 et 3.0.</li>
     *                     <li>si le commentaire n'est pas instancié.</li>
     *                     </ul>
     * @throws NotReview   si l'avis n'existe pas.
     * @throws NotMember   si un des membres n'existe pas.
     * @throws NotItem     si l'item n'existe pas.
     * @throws SelfGrading si l'auteur de l'opinion essaye de noter son propre avis.
     */
    public float gradeReviewItemBook(String pseudo, String password, String reviewPseudo, String reviewTitle, float grade) throws NotReview, NotMember, BadEntry, NotItem, SelfGrading {
        return gradeReview(Book.class, pseudo, password, reviewPseudo, reviewTitle, grade);
    }

    /**
     * Note un avis sur un film.
     *
     * @param pseudo       le pseudo du membre noteur.
     * @param password     le password du membre noteur.
     * @param reviewPseudo le pseudo de l'auteur de l'avis.
     * @param reviewTitle  le titre de l'item.
     * @param grade        la note attribué à l'avis.
     * @return la nouvelle note moyenne associé à l'avis.
     * @throws BadEntry    <ul>
     *                     <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                     <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                     <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                     <li>si la note n'est pas comprise entre 1.0 et 3.0.</li>
     *                     <li>si le commentaire n'est pas instancié.</li>
     *                     </ul>
     * @throws NotReview   si l'avis n'existe pas.
     * @throws NotMember   si un des membres n'existe pas.
     * @throws NotItem     si l'item n'existe pas.
     * @throws SelfGrading si l'auteur de l'opinion essaye de noter son propre avis.
     */
    public float gradeReviewItemFilm(String pseudo, String password, String reviewPseudo, String reviewTitle, float grade) throws NotReview, NotMember, BadEntry, NotItem, SelfGrading {
        return gradeReview(Film.class, pseudo, password, reviewPseudo, reviewTitle, grade);
    }

    /**
     * Note un avis sur un item.
     *
     * @param reviewKlass  le type de l'item ({@code Book} ou {@code Film}).
     * @param pseudo       le pseudo du membre noteur.
     * @param password     le password du membre noteur.
     * @param reviewPseudo le pseudo de l'auteur de l'avis.
     * @param reviewTitle  le titre de l'item.
     * @param grade        la note attribué à l'avis.
     * @return la nouvelle note moyenne associé à l'avis.
     * @throws BadEntry    <ul>
     *                     <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                     <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                     <li>si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                     <li>si la note n'est pas comprise entre 1.0 et 3.0.</li>
     *                     <li>si le commentaire n'est pas instancié.</li>
     *                     </ul>
     * @throws NotReview   si l'avis n'existe pas.
     * @throws NotMember   si un des membres n'existe pas.
     * @throws NotItem     si l'item n'existe pas.
     * @throws SelfGrading si l'auteur de l'opinion essaye de noter son propre avis.
     */
    private float gradeReview(Class<?> reviewKlass, String pseudo, String password, String reviewPseudo, String reviewTitle, float grade) throws BadEntry, NotReview, NotMember, NotItem, SelfGrading {
        Review review = findMatchingReview(reviewKlass, reviewTitle, reviewPseudo);
        Member member = findMatchingMember(pseudo, password);

        if (new MemberKey(pseudo).equals(new MemberKey(reviewPseudo))) {
            throw new SelfGrading("Member is grading his own review.");
        }

        // On recherche la précédente note donné par le membre pour une review.
        // - Si elle existe on la met à jour.
        // - Sinon on en crée une.
        ReviewGrade reviewGrade = member.findReviewGrade(review);
        if (reviewGrade == null) {
            reviewGrade = new ReviewGrade(review, member, grade);
            review.updateAverageGrade(grade);
            member.addReviewGrade(reviewGrade);
        } else {
            float oldGrade = reviewGrade.getGrade();
            reviewGrade.update(grade);
            review.updateAverageGrade(oldGrade, grade);
        }

        return review.getAverageGrade();
    }

    /**
     * Cherche un item dans le SocialNetwork.
     *
     * @param klass le type de l'item ({@code Book} ou {@code Film}).
     * @param title le titre de l'item.
     * @return l'item, si il a été trouvé.
     * @throws NotItem  si l'item n'existe pas.
     * @throws BadEntry si le titre n'est pas instancié ou a moins de 1 caractère autre que des espaces.
     */
    private Item findMatchingItem(Class<?> klass, String title) throws NotItem, BadEntry {
        if (!Item.titleIsValid(title)) {
            throw new BadEntry("Item title does not meet the requirements.");
        }

        Item item = items.get(new ItemKey(klass, title));

        if (item == null) {
            throw new NotItem("Item not found.");
        }

        return item;
    }

    /**
     * Cherche une review dans le SocialNetwork.
     *
     * @param klass  le type de l'item ({@code Book} ou {@code Film}).
     * @param title  le titre de l'item.
     * @param pseudo l'auteur de la review.
     * @return la review, si elle a été trouvé.
     * @throws NotReview si la review n'existe pas.
     * @throws NotMember si l'auteur n'existe pas.
     * @throws BadEntry  <ul>
     *                   <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                   <li>si le titre de l'item n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   </ul>
     * @throws NotItem   si l'item n'existe pas.
     */
    private Review findMatchingReview(Class<?> klass, String title, String pseudo) throws NotReview, NotMember, BadEntry, NotItem {
        if (!(Member.pseudoIsValid(pseudo))) {
            throw new BadEntry("Pseudo and/or title does not meet the requirements.");
        }

        Member member = members.get(new MemberKey(pseudo));

        if (member == null) {
            throw new NotMember("Member not found.");
        }

        Item item = findMatchingItem(klass, title);
        Review review = member.findReview(item);

        if (review == null) {
            throw new NotReview("Review not found.");
        }

        return review;
    }

    /**
     * Identifie un membre dans le SocialNetwork.
     *
     * @param pseudo   le pseudo du membre.
     * @param password le mot de passe du membre.
     * @return le membre, si il a été trouvé et que les identifiants sont corrects.
     * @throws NotMember si le membre n'existe pas.
     * @throws BadEntry  <ul>
     *                   <li>si le pseudo n'est pas instancié ou a moins de 1 caractère autre que des espaces.</li>
     *                   <li>si le password n'est pas instancié ou a moins de 4 caractères autres que des leadings or trailing blanks.</li>
     *                   </ul>
     */
    private Member findMatchingMember(String pseudo, String password) throws NotMember, BadEntry {
        if (!(Member.pseudoIsValid(pseudo) && Member.passwordIsValid(password))) {
            throw new BadEntry("Pseudo and/or password does not meet the requirements.");
        }

        Member member = members.get(new MemberKey(pseudo));

        if (member == null) {
            throw new NotMember("Member not found.");
        }

        if (!member.checkCredentials(pseudo, password)) {
            throw new NotMember("Invalid credentials.");
        }

        return member;
    }

    /**
     * Compte le nombre d'item d'un type donné contenus dans le SocialNetwork.
     *
     * @param klass le type de l'item ({@code Book} ou {@code Film}).
     * @return le nombre d'items du type donné contenus.
     */
    private int countItems(Class<?> klass) {
        int count = 0;

        for (ItemKey key : items.keySet()) {
            if (key.classEquals(klass)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Obtenir une représentation textuelle du <i>SocialNetwork</i>.
     *
     * @return la chaîne de caractères représentation textuelle du <i>SocialNetwork</i>.
     */
    public String toString() {
        String output = "";

        output += "SocialNetwork" + "\n";
        output += nbMembers() + " members" + "\n";
        output += nbBooks() + " books" + "\n";
        output += nbFilms() + " films" + "\n";

        return output;
    }
}
