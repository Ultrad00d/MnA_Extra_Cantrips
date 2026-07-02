
# Customer Meeting Transcript

> **Project:** Mana and Artifice Extra Cantrips<br>
> **Date:** 2026-06-28<br>
> **Location/Platform:** in-person<br>
> **Recording:** Yes  -  customer consented to recording and private sharing with instructors<br>
> **Publication:** Yes  -  customer consented to publishing the sanitized transcript in the repository

## Participants

| Name | Role |
|---|---|
| ultradood | Interviewer |
| notwindstone | Note taker |
| l1n0n | Note taker |
| Dima280807 | Note taker |

---

[0:00:00] Well, so, in general, we have icons for the cantrips.

Is it recording?

It's recording.

Great. Mana's run out. Perfect. How do we restore it? Creative mode. Convenient. So... the top knot. And the little diamond.

[0:00:30] So here's what the lightning one looks like.

What do you think of the idea? The more complex the symbols, the stronger.

[0:00:44] I think...

If it's too much hassle, you don't have to do it.

Well, the problem is you can't see what symbols the player has specifically placed.

Wait, you can't?

Not through the API. There's no function for that. So you'd have to look at it yourself, figure it out independently.

If you're not interested or don't know how to implement it, you don't have to. I won't insist. It's totally optional.

[0:01:14] You can summon the spectral bed. Here it is, kind of translucent.

By the way, it's pretty cool.

Oh, super, I already showed it. Here we are sleeping on it.

Up close it looks cooler than from far away.

And it breaks in the morning.

That's cool, like amethyst breaking, right?

Yes.

The particles are nice.

There's a spectral boat too.

[0:01:43] Here's this thing.

I see the donkey is still drawn in Paint.

Yes. The donkey, well, ran out of mana again. Need creative mode.

Hey, why isn't it restoring mana don't know.

Seems like something bugged. Spent more mana than it should have. You've got mana corruption going on.

[0:02:13] So, this and this. This was the spectral bed.

Wait, hold on a second. Go into the inventory.

What inventory?

Just go into the player's own inventory.

Okay.

Open it. Do you have nothing equipped?

No.

Okay, because for some reason your mana was draining.

[0:02:39] And you don't have a feature where the bed drains your mana while it exists?

No, not yet...

Because look, it's still draining. You finished drawing, but it's still dropping.

That's probably from a saturation effect.

[0:03:01] What could that be from?

Well, apparently there was a saturation effect. I removed the effect and it disappeared.

That's weird, but okay. So, there's the spectral boat.

[0:03:15] Here it is, let's sail where we need to. We're arriving now.

A turtle is making its way to the sea overland.

So about the boat — you can see the insides.

[0:03:34] Did the boat eat them or what's happening with movement?

I don't know, you can walk in the boat, in the vanilla one. Oh, in this one too, it's just I didn't spawn high enough, apparently. You say the insides are visible?

If you look inside, they're a bit bugged. They can be removed.

This thing?

Yes.

[0:03:54] Could you just put a chest on its bottom at the same level as the boat's bottom? Maybe that's what's happening.

Well, it's the original texture from Minecraft, just transparent.

Right, theirs isn't transparent, so...

We could just erase the insides...

Hey, is it possible to put that glowing thing inside the chest from the cache?

[0:04:21] Probably. Well, theoretically anything is possible.

Like, is that hard?

Add it separately.

I just don't know how it... If it's an entity, no problem. If it's somehow a block...

Like, you'd need to add that glowing thing inside the chest.

Probably.

I don't know if it would look better or worse.

[0:04:50] And, in general, if you right-click on the boat for now, here opens a chest. Let's put a piece of leather here. Piece of leather. Now let's summon our new friend. The spectral donkey. Right into the water.

For swimming, right?

Yes. Let's open its storage and take this leather right from there. It's not here either.

The little bags — did you draw those?

The little bags are all drawn by Alexander. Not bad that he can do that.

Hey, listen, it turned out great.

This is the first time I've seen him swim that fast.

So why do we need the boat?

[0:05:32] Now it'll land, it really will. Maybe it needs one more method added so it swims properly. I kind of forgot about that when adding it.

It moves across water like across land.

It just doesn't sink and moves at the same speed.

[0:06:03] In general, well, good testing.

Did you implement the disappearance for the donkey?

Yes, the donkey, like the boat, it disappeared.

We got off it — go into the inventory, let's put it in the inventory while we wait.

In its inventory?

Yes.

Oh, its inventory should also close when the donkey disappears.

[0:06:23] Put some leather there and let's just wait.

Okay let's wait. It'll disappear soon, 15 seconds left.

By the way, I haven't checked with the boat what happens. I just checked whether it disappears or not.

And this inventory opens with shift right-click, right?

[0:06:38] I made it open with right-click. Dayan fixed it to also open differently.

And the donkey restart timer — does restart with right-click? Restarts the timer.

By the way, it... Oh, there. So it improved, it didn't even close.

No, it's just a question — when we get into the chest, does the timer restart or not?

[0:07:00] No, it doesn't reset. I wanted to make it reset, or even not count at all so you could just...

How fun it's going to be to write this transcript, translate my... "re-over-eats-itself."

And Dayan fixed it so you can press E here and it opens this chest, not the donkey's inventory, and...

The little bags are actually top-tier, on the sides of the donkey.

[0:07:29] And the boat should be the same, right? If we open it. Yes, this is it...

Oh wow, Dayan already did it.

That's Dayan, I went into his branch. He already fixed it, made it. Right a pull request. Before the interview even started.

Well, top-tier. And Rift doesn't overlap?

No, this is completely... And it doesn't overlap with Ender Chest either?

Yes.

Well, that's good.

It's all separate.

Good.

[0:07:52] Rift, it turns out, opens a huge inventory, like a double chest with 54 slots. I thought it would open a small one.

Rift has a Precision modifier.

Yes, it's either 0 or 1.

Yes, if you set it to 1, the vanilla Ender Chest opens. If you set it to 0, the Rift itself opens.

Let it sit with items in the boat for now. While I look for the book. Here it is.

[0:08:24] And we'll do touch and Rift. I think it's a regular... Like this one. And at one. This is this one.

Why did it change color?

Because it's the Ender Chest, apparently.

You know what's funny? You can open Rift via Self, and when you do Self, you don't see the color of that part. It opens inside you.

Yes.

[0:08:58] Its description says... Well, in its spell description it says I summoned it somewhere, dipped my hand in there, and my items were there. That's why it summons like this.

Everything that's summoned: Spectral Workbench, Spectral Anvil, Rift. You can cast it somewhere in space for it to appear. Or you can cast it on yourself and it just opens an inventory window. So you won't see the texture.

Remember I told you about gates today?

Gates?

Yes, when we were talking about portals.

I don't remember gates. Put on Occulus. Scroll down in rituals to the fourth tier. Recharge... Where? Did I mix it up somehow? Oh, here's Gate. Anyway, in short, it opens a portal, a permanent portal. Different factions have different portal designs. Flowers appear for the Fae, a Grave opens for the Undead. Well, that's secondary. So, what else did you want to show me?

I have Empower, but only for damage.

There is an Empower?

Yes.

Where?

[0:10:33] It was in a separate branch. In the video that was shown, nothing else has been made yet. I was going to work on it this evening.

And I was curious to look at what combinations you set.

I was going to work on that this:11:16] If you're ready to make some structures or any other mechanics, you think I'll say it's bad, or good, or what could be improved. And the structure idea is also cool. How hard is it to implement?

[0:11:40] Generally, when I talked to authors about adding new structures, they said you can convert a structure into an array through some additional tool and then just put that array into the addon with a list of spawning structures.

No, adding a structure is not a problem. It's most likely done through the data tab.

They meant something. There's some Minecraft addon where you build a structure in the real world, then select the area, and it saves as a file. Then you just insert that file into your addon and set the spawn conditions and everything.

[0:12:32] Most likely it's done with a datapack, since it's all done with a datapack. So I've written out all these possible shapes and their levels. So here, for example, for lightning — a square, a circle, and this lightning bolt symbol is tier 3, specifically tier 1. So the cantrip turns out to be tier 3. I haven't thought of 2-3 yet.

Well, that's also a separate question — what tier to put it at.

So, let's say we have some test subject.

Yeah. What is this? Is this that famous zeroing? Or what? What are you going to show me?

Well, with the staff, probably. Depending on tier, an effect is applied to you.

What's that icon? I couldn't make it out.

Damage with arrows.

Just letters for now, just.

Show me again.

[0:13:42] It already depends on tier, the level.

I saw in the description what you planned.

We're on the fifth tier now, so to speak, I would put 5 damage right now. And so it goes...

You know, if you have no ideas for what icon to make for mana damage, if I understand correctly, it's associated with the Ender rune. So you can just... I mean, go into the item, write... Ender. No, then runes. Just runes. No? Then what?

[0:14:36] Okay, scroll to the blue outlines. If you can't think of a normal icon, you can just draw an outline, some upward arrow. If you come up with your own thing, I won't object.

[0:15:07] And the boost damage you applied — does it show up externally or not yet?

Only with an effect. Right now it's simpler to apply via command.

Well, obviously, you can't do everything at once because it's complicated. If you decide not to do visualization, you don't have to. I just don't want to overwork you, I'm afraid of overloading you.

[0:15:33] I'm just not a strong artist, I drew this in five minutes. It's just a placeholder for now.

I can see you're enjoying doing it, so the main thing is don't overwork yourself.

[0:15:50] In general, three levels of the effect, depending on tier it's applied. And depending on tier the duration is also different.

By the way, regarding 15 seconds — we'll need to see how much time is needed to draw the next cantrip. Well, for one more degree. To understand whether 15 or 20 seconds is enough or not.

[0:16:15] That's just a balance question.

[0:16:23] So at tier 2, where you can have a maximum of 1 buff, you're given just 15 seconds. At tier 3, where you can already have 2 effects, you get 20 seconds. Then, where 2 effects and one of them can be maximum level 2, meaning both at level 2, you get 30 seconds.

[0:16:49] Draw something, enhance it, draw a second thing, enhance it. There, I don't know, maybe 20 seconds pass and 10 seconds remain for using the spell.

Do you remember how else you can draw symbols?

You draw and retrieve some jar.

Yes-yes-yes.

Take a vial.

A regular one?

Yes. Draw some symbol. And now right-click.

[0:17:28] These vials can be accumulated and then thrown in the right order.

[0:18:00] And also, you know what's interesting? There's also this. Oh, by the way, this might be useful for your structure. Wait, does it cast instantly? Without a staff?

Yes.

Oh, I didn't know that. And try casting a regular cantrip. Does it also cast without a staff?

[0:18:35] You know, maybe it creates two single symbols, so you just need a small cooldown between throws. You've got a quadruple diamond there, that's not a simple shape. So try throwing one somewhere first, then a small cooldown.

And it throws into that, and because another one appears above it.

Strange.

And when two are next to each other, instantly.

So it doesn't always work. Try making a vanilla cantrip, fire for example. Like this.

[0:19:34] Break it with the staff, left click.

Good. Got to grab the rod while the cooldown is going. So, look — take a mana projector. Or mana weaving projector.

[0:20:33] See, you're consuming mana to charge the projector. Also take mana weaving, just take it. Mana weaving. Hey, can you press Z? No, it doesn't disappear. Switch to survival mode to understand how it works. Right-click. Take half a stack of vintium metal in your hands. Next take chalk. Oh, there is chalk. Take a door in your hands. Good, now draw a plus sign. No, not plus, cross. Start the ritual. Okay, right-click.

[0:21:46] Just buy a few of any. These are just things with symbols attached.

This is what I want.

[0:22:08] I don't know why it's like this. Okay, come out now. Start clicking on the projector. Now click on it with an empty hand. Step aside. See, it appeared. Oh, click on the projector with the other hand. Well, I don't use the projector, but I know it exists. In theory, if you want to do studying, you can make a structure where, say, these projectors just stand in different rooms. But you need to somehow specify what symbols are in them, and so that if it's all locked behind achievements, the achievements would trigger from right-clicking not a normal projector, but specifically the projector in your structure. Here. That's one possible option, and I don't know how hard it is to do.

[0:23:22] So if you want to...

You just need to at least confirm that we'll have some structure, because right now it's not even decided.

Well, this is just in case, I'm giving options. I'm not saying I expect this from you. It's just... if you're ready, here's how you could try. I don't know if you can specify what symbols the projectors will stand with, so that you can specify in the structures what spell will be stored in it. One of the possible mechanics if you decide to do this. This doesn't mean you need to make structures. You just said there's this version, if you decide to do it, here's an option.

I'm just saying this is simpler.

I'm just throwing ideas at you. If you like them, you can do them. If you don't, you don't have to.

[0:24:25] I'm giving you creative freedom in a way.

Well, things are things. You can't place a ladder sideways.

You know what you can do? Although diagonals are tricky.

[0:24:52] In Mana and Artifice you saw blocks, right? Arcane Stone, Arcane Stone, but what's the other one called? Okay, just see the item list? Scroll down until you see blue stones in large quantities.

[0:25:26] There's one with amethyst, see? And there are different lines on the blocks. And so we could try to draw through this. There are corner pieces, side pieces. But no diagonal ones.

[0:26:45] I wanted to rotate it. I can't do it with a command. Because here it's quaternions, I have no idea how to rotate them.

[0:26:54] Oh, I should know but I can't remember. Either in vanilla Minecraft or there's some mod for Minecraft that adds a stick that lets you rotate blocks.

Anyway, you can do this with Axiom, but I just don't have it installed here. Anyway, we'll just rotate it and it'll look like this block. You can already see it looks like this. And you can just draw anything on it. Just put a barrier in its place, conditionally. Although... Oh, it doesn't matter at all. You can do it however you want, really. These blocks don't add any mod at all, they're purely vanilla. You can just rotate this model like this and do it.

[0:27:55] And so on the wall you'd have a drawn square.

So you're proposing adding special vertical half-blocks, steps? Specifically for this?

I'm saying you could do it easier by just adding...

Yes, you could draw with these lined blocks.

Yes, you can do it however you want. You could just add separate textures of these... In short, an entity that has this texture...

I think carved sandstone with patterns would be good here.

[0:28:35] I just think if we do it this way, make one where... The design — you press right-click on it, it disappears, and the player consumes it. The easiest way, I think, is to just make...

Of course, how do you flip it upside down? Well, that's it, it broke.

[0:28:57] And so the easiest way, I think, is to just make an entity that appears in front of a wall like this, like a painting — a painting is also an entity. You can hit the painting and it disappears, but we don't need that — we need you to click on it and it disappears but gives you an achievement. And its texture would be, well, not a painting, but a square.

What an inconvenient keybinding for me. Bad sensitivity, bad everything, I'm thinking, what did I sign up for? Come on. You need corners. What angle did it become? This one, right?

[0:29:49] Anyway, the idea is clear. Just rotate them like this.

Right now I'll finish drawing, I'll finish drawing.

Okay, no problem. I'm just saying how it'll turn out.

[0:30:05] It's wide here, we could actually remove one of these. So what's there? I made two, right?

Oh, it placed it.

And how is this? What is this?

It's a display block. A... what's it called? I don't remember what it's called in short.

Where is this corner? In what position? Oh, here it is.

[0:30:37] My mouse sensitivity is low, I'm really suffering with this.

There's a debug stick in vanilla Minecraft that rotates a stick.

Let's use it.

There! Just a little bit left.

Is there still about half the work left?

No, it's fine, now we'll place your blocks. There!

Yeah.

I just keep forgetting the corners I was placing them at.

[0:31:05] Yes-yes-yes.

So, about the Paterianguli. Well, inside just put an empty little stone. Not for broadcast.

[0:31:32] Let's go.

There! These are our local cantrips. A cantrip is a focus.

Oh, that's how it's translated, right?

Well, Linar has a focus.

Cantrip translates to "focus" in Russian.

Yes-yes.

So, once again — until you're ready, this is just for fun. If you don't do it, I won't be mad at you. So what's next? Oh, that's it, nothing else to record?
