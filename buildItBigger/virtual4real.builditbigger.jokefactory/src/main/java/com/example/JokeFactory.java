package com.example;

public class JokeFactory {
    private String[] vJokes = new String[20];


    public JokeFactory(){
        vJokes[0] = "Ordinarily, staring is creepy. But if you spread your attention across many individuals, then it's just people watching.";
        vJokes[1] = "Here’s some advice: At a job interview, tell them you’re willing to give 110 percent. Unless the job is a statistician.";
        vJokes[2] = "A farmer counted 196 cows in the field. But when he rounded them up, he had 200.";
        vJokes[3] = "The problem with math puns is that calculus jokes are all derivative, trigonometry jokes are too graphic, algebra jokes are usually formulaic, and arithmetic jokes are pretty basic. But I guess the occasional statistics joke is an outlier.";
        vJokes[4] = "Hear about the statistician who drowned crossing a river? It was three feet deep on average.";
        vJokes[5] = "Probably the worst thing you can hear when you’re wearing a bikini is: Good for you!";
        vJokes[6] = "The closest a person ever comes to perfection is when he fills out a job application form.";
        vJokes[7] = "Instagram is just Twitter for people who go outside.";
        vJokes[8] = "If people say they just love the smell of books, I always want to pull them aside and ask, To be clear, do you know how reading works?";
        vJokes[9] = "We get it, poets: Things are like other things.";
        vJokes[10] = "I’m writing my book in fifth person, so every sentence starts out with: I heard from this guy who told somebody …";
        vJokes[11] = "You’ll never be as lazy as whoever named the fireplace.";
        vJokes[12] = "A Canadian psychologist is selling a video that teaches you how to test your dog’s IQ. Here’s how it works: If you spend $12.99 for the video, your dog is smarter than you.";
        vJokes[13] = "Why do dogs always race to the door when the doorbell rings? It’s hardly ever for them.";
        vJokes[14] = "So what if I can’t spell Armageddon? It’s not the end of the world.";
        vJokes[15] = "Halloween is the beginning of the holiday shopping season. That’s for women. The beginning of the holiday shopping season for men is Christmas Eve.";
        vJokes[16] = "Just found the worst page in the entire dictionary. What I saw was disgraceful, disgusting, dishonest, and disingenuous.";
        vJokes[17] = "I admit that I live in the past, but only because housing is so much cheaper.";
        vJokes[18] = "If con is the opposite of pro, then isn’t Congress the opposite of progress?";
        vJokes[19] = "What should you do when you see an endangered animal eating an endangered plant?";
    }

    public String getJoke(int nPos){
        if(nPos < 1 || nPos > 20) return vJokes[1];

        return vJokes[nPos - 1];
    }
}
