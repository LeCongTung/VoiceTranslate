package com.example.voicetranslate.shows

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voicetranslate.R
import com.example.voicetranslate.adapters.AdapterExample
import com.example.voicetranslate.models.Example
import com.example.voicetranslate.screens.HomeActivity
import kotlinx.android.synthetic.main.activity_show_content.*

class ShowContentActivity : AppCompatActivity() {

    private val newArrayList: ArrayList<Example> = arrayListOf()
    private var searchArrayList: ArrayList<Example> = arrayListOf()
    private var filteredNames = ArrayList<Example>()

    private val myAdapter: AdapterExample by lazy { AdapterExample(newArrayList) } //Chi khoi tao khi duoc goi

    private val exampleBank: Array<String> by lazy {
        arrayOf(
            "Can I withdraw money on my credit card here?", "Do you need to see my passport?", "How late is the bank open?", "How late is the exchange point open?", "I need a deposit slip", "I need a withdrawal slip.", "I need some money.", "I would like to open a checking account.", "I would like to open a savings account.", "I would like to open an account.", "I'd like to deposit some money.", "I'd like to withdraw some money.", "Put it on my American Express.", "Put it on my MasterCard.", "Put it on my Visa.", "This is my identification"
        )
    }

    private val exampleBasic: Array<String> by lazy {
        arrayOf(
            " A couple of weeks.", "A family visit.", " A lot of people don't like it.", "About a week ago.", "Absolutely not.", "Absolutely right.", "Ace of spades.", "Admit it.", " After that.", "After you.", "All night long.", "All the time?", "And I will always love you.", "Another day.", "Answer the question.", "Anybody here?", "Anybody home?", "Anything is possible.", "April", "Are you a teacher?", "Are you alone?", "Are you alright?", "Are you angry?", "Are you awake?", "Are you crazy?", "Are you free today?", "Are you free tonight?", "Are you gay?", "Are you happy now?", "Are you happy?", "Are you insane?", "Are you jealous?", "Are you joking?", "Are you kidding me?", "Are you listening to me?", " Are you listening to music?"
        )
    }

    private val exampleBeautyCare: Array<String> by lazy {
        arrayOf(
            "A soft perm, please.", "Are you busy now?", "Could you blow-dry my hair, please?", "Could you curl the ends inward, please?", "Could you curl the ends outward, please?", "Could you cut it short, please?", "Could you file and shape my nails, please?", "Could you fix the style with hair spray, please?", "Could you part my hair in the middle, please?", "Could you part my hair on the right side, please?", "Could you trim my beard, please?", "Could you trim my moustache, please?", "Could you use mousse, please?", "Curly hair.", "Dark hair.", "Do you do hair removal?", "How long will I have to wait?", "How long will it take?"
        )
    }

    private val exampleCallPolice: Array<String> by lazy {
        arrayOf(
            "Be quick.", "Because of that.", "Can I go now?", "Can I have it reissued?", "Can I see your driving license?", "Do not worry.", "Don't be afraid.", "Drop it.", "He cheated me.", "He got lost while walking in the woods.", "Help!", "How do you spell your name?", "How long you been here?", "I am afraid.", "I am looking for.", "I am scared.", "I can't believe.", "I did nothing."
        )
    }

    private val exampleCommunication: Array<String> by lazy {
        arrayOf(
            "A letter.", "Accessible.", "Add to my shortlist.", "Allow full access.", "Are you still there?", "Best regards.", "Call back.", "Can I access the Internet here?", "Can I dial direct?", "Can I talk with you?", "Can you dial for me?", "Can you talk for me?", "Could I leave a message?", "Could you please direct me to the post office?", "Could you send it to this address?", "Create an account.", "Customs is holding the package.", "Customs problem.",
        )
    }

    private val exampleFoodDrink: Array<String> by lazy {
        arrayOf(
            "A piece of cake.", "Brazil nuts", "Brie", "Brussels sprouts", "Caesar salad", "Can I get some water?", "Champagne", "Chicken nugget.", "Chocolate cake.", "Cold water.", "Cornstarch.", "Cutting board.", "Damaged goods.", "Danish pastry", "Date of expiry.", "Did you finish your lunch?", "Dinner is ready.", "Dinner plate."
        )
    }

    private val exampleHealth: Array<String> by lazy {
        arrayOf(
            "After meals?", "Am I allowed to get out of bed?", "Are you feeling better?", "Are you in pain?", "Are you pregnant?", "Be careful not to catch a cold.", "Be patient.", "Before meals?", "Belly button.", "Both sides.", "Can I continue my trip?", "Can I have some antiseptic cream, please?", "Can I have some condoms, please?", "Can I have some insect repellent, please?", "Can I have some tissues, please?", "Can I have some toothpaste, please?", "Can I have something for a cold?"
        )
    }

    private val exampleHotel: Array<String> by lazy {
        arrayOf(
            "Access denied.", "Accounts payable.", "Additional services.", "Air condition.", "All included", "Any questions.", "Are there any spare fuses?", "Are you looking for something?", "At what times are meals served?", "Bed and breakfast (BB)", "Can I get a blanket?", "Can I know your account number?", "Can I leave a message?", "Can I pay later?", "Can I use the card?", "Can I walk or should I take a taxi?", "Can you do me a favor?", "Can you open the door?"
        )
    }

    private val exampleRestaurant: Array<String> by lazy {
        arrayOf(
            "A green salad with French dressing.", "A little more, please.", "A tea with lemon, please.", "All-inclusive.", "Almost done.", "Are there any authentic restaurants around here?", "Are there any cheap restaurants around here?", "Are there any good restaurants around here?", "Are there any vegetarian restaurants around here?", "Are you ready to order?", "Black tea.", "Can I get a cup of tea?", "Can I get change?", "Can I have a beer?", "Can I have anything to drink?", "Can I have it right away?", "Can I have some water, please?", "Can I pay by credit card?"
        )
    }

    private val exampleTransport: Array<String> by lazy {
        arrayOf(
            "Anybody can help me here?", "Are the repairs covered by my insurance?", "Are there any discount fares for multiple trips?", "Are you free?", "Buckle up.", "By car.", "Call a doctor.", "Call the fire department.", "Call the police.", "Can I buy a monthly pass?", "Can I buy a weekly pass?", "Can I park my car here?", "Can we settle the matter between ourselves?", "Can you check it for me?", "Can you drive?", "Can you lend me a jack?", "Can you repair the car?", "Can you send a mechanic?"
        )
    }

    private val exampleRepairLaundry: Array<String> by lazy {
        arrayOf(
            "(Liquid) detergent", "Any problem.", "As soon as possible.", "Attempting repairs.", "Be careful with a hammer.", "Bleach", "Can you fix the zipper?", "Can you fix this?", "Can you mend these shoes for me?", "Clean up.", "Coins", "Colors", "Darks", "Delicates", "Do not bleach.", "Do not dry clean.", "Do not iron.", "Do you have a battery for this?"
        )
    }

    private val exampleShopping: Array<String> by lazy {
        arrayOf(
            "An umbrella.", "Antique store", "Bakery", "Bank account.", "Best before ...", "Black Friday.", "Black and white.", "Body lotion.", "Bookstore", "Camera store", "Can I buy it tax-free?", "Can I get a discount?", "Can I have a bag, please?", "Can I touch it?", "Can I try it?", "Can you check my size?", "Can you give me a better price?"
        )
    }

    private val exampleSightseeing: Array<String> by lazy {
        arrayOf(
            "Amusement park.", "Are there any movie theaters near here?", "Are there any seats left for tonight?", "Big city.", "Breathtaking landscape.", "Built in", "Can I take a photo?", "Can I take a picture with you?", "Can you recommend a concert?", "Can you recommend a guide?", "Can you take a picture?", "Can you tell me the way to the airport?", "Can you tell me the way?", "Closed", "Could you please arrange the trip for me?", "Could you please tell me what museums there are?", "Could you please tell me what theatres there are?", "Could you show me the way to the beach?"
        )
    }

    private val exampleSport: Array<String> by lazy {
        arrayOf(
            "Are there electrical outlets on site?", "Are there reefs or strong currents here?", "Are there showers on site?", "Are there trash cans on site?", "Are there underwater currents?", "Are you interested in music?", "At the gym.", "Back to the future.", "Baseball player.", "Can I go diving there?", "Can I go hang-gliding there?", "Can I go surfing there?", "Can I go water-skiing there?", "Can I rent a diving suit and equipment?", "Can you pass me the water?", "Can you swim?", "Clap"
        )
    }

    private val exampleStudying: Array<String> by lazy {
        arrayOf(
            "Are you currently employed?", "Are you going to school?", "Are you still working?", "Business partner.", "Call this number.", "Correct answer.", "Departure from warehouse.", "Did you do your homework?", "Do you have any questions?", "Do you like your job?", "Do you mind?", "Elementary school.", "Exercise book.", "First lesson.", "Full time.", "Get back to work.", "Give me a break.", "Give me a list."
        )
    }

    private val exampleTraveling: Array<String> by lazy {
        arrayOf(
            "A one-way ticket, please.", "A return-ticket, please.", "Aisle seat.", "Alarms", "An air sickness bag, please.", "Are there any delays?", "Are there any reduced fares?", "Are there any seats on the bus?", "Are there any seats on the flight?", "Are there any seats on the train?", "Around the world.", "Arrivals", "Average speed.", "Baggage claim", "Boarding gate.", "Boarding pass", "Boarding time.", "Border crossing"
        )
    }

    lateinit var value: String
    lateinit var intentDisplayFrom: String
    lateinit var intentLanguageFrom: String
    var intentFlagFrom: Int = 0
    lateinit var intentDisplayTo: String
    lateinit var intentLanguageTo: String
    var intentFlagTo: Int = 0
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_content)

//        Get text from image
        value = intent.getStringExtra("value").toString()
        intentDisplayFrom = intent.getStringExtra("displayFrom").toString()
        intentLanguageFrom = intent.getStringExtra("languageFrom").toString()
        intentFlagFrom = intent.getIntExtra("flagFrom", R.drawable.ic_flag_english)
        intentDisplayTo = intent.getStringExtra("displayTo").toString()
        intentLanguageTo = intent.getStringExtra("languageTo").toString()
        intentFlagTo = intent.getIntExtra("flagTo", R.drawable.ic_flag_vietnamese)

        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                filters(p0.toString())
            }
        })

//        Get data from previous screen
        title = intent.getStringExtra("title").toString()
        title_content.text = title

//        Excute button -- when click button
        btn_back.setOnClickListener {

            onBackPressed()
        }

//        Api
        list_item.adapter = myAdapter
        list_item.layoutManager = LinearLayoutManager(this)
        list_item.setHasFixedSize(true)
        getDataExample()
    }

    //    Function -- Click back button to close app
    override fun onBackPressed() {

        val intent = Intent(this@ShowContentActivity, ShowOfflinePhraseBookActivity::class.java)
        intent.putExtra("value", value)
        intent.putExtra("displayFrom", intentDisplayFrom)
        intent.putExtra("languageFrom", intentLanguageFrom)
        intent.putExtra("flagFrom", intentFlagFrom)
        intent.putExtra("displayTo", intentDisplayTo)
        intent.putExtra("languageTo", intentLanguageTo)
        intent.putExtra("flagTo", intentFlagTo)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
    }

    //    Get data
    private fun getDataExample() {

        val arrayDisplay: Array<String> = choiceArray(title)
        newArrayList.clear()
        for (i in arrayDisplay.indices) {
            val example = Example(arrayDisplay[i])
            newArrayList.add(example)
        }

        searchArrayList = newArrayList
        list_item.adapter = myAdapter

        myAdapter.setOnItemClickListener(object : AdapterExample.onItemClickListener {
            override fun onItemClick(position: Int) {

                val titleToContent = if (filteredNames.size == 0)
                    searchArrayList[position].example.toString()
                else
                    filteredNames[position].example.toString()

                val intent = Intent(this@ShowContentActivity, HomeActivity::class.java)
                intent.putExtra("value", titleToContent)
                intent.putExtra("displayFrom", intentDisplayFrom)
                intent.putExtra("languageFrom", intentLanguageFrom)
                intent.putExtra("flagFrom", intentFlagFrom)
                intent.putExtra("displayTo", intentDisplayTo)
                intent.putExtra("languageTo", intentLanguageTo)
                intent.putExtra("flagTo", intentFlagTo)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_blur, R.anim.slide_blur)
            }
        })
    }

    //    Function -- Filter
    private fun filters(text: String) {

        filteredNames.clear()
        searchArrayList.filterTo(filteredNames) {

            it.example.toString().lowercase().contains(text.lowercase())
        }
        myAdapter.filterList(filteredNames)
    }

    private fun choiceArray(title: String): Array<String> {

        return when (title) {

            "Bank" -> exampleBank
            "Basic" -> exampleBasic
            "Beauty Care" -> exampleBeautyCare
            "Calling for Police" -> exampleCallPolice
            "Communication Means" -> exampleCommunication
            "Food and Drinks" -> exampleFoodDrink
            "Health and Drugstore" -> exampleHealth
            "Hotel" -> exampleHotel
            "In the Restaurant" -> exampleRestaurant
            "Local Transport"-> exampleTransport
            "Repairs and Laundry"-> exampleRepairLaundry
            "Shopping"-> exampleShopping
            "Sightseeing"-> exampleSightseeing
            "Sport and Leisute"-> exampleSport
            "Studying and Work"-> exampleStudying
            else -> exampleTraveling
        }
    }
}