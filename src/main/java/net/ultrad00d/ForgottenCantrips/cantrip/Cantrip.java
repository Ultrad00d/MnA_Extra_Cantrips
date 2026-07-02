// package net.ultrad00d.ForgottenCantrips.cantrip;

// import com.mna.api.cantrips.ICantrip;

// import net.minecraft.server.TickTask;
// import net.minecraft.server.level.ServerLevel;
// import net.minecraft.world.InteractionHand;
// import net.minecraft.world.entity.player.Player;

// public abstract class Cantrip {
//     public Cantrip(Player player, ICantrip cantrip, InteractionHand hand) {
//         delay(player, cantrip, hand);
//     };
//     public abstract void run(Player player, ICantrip cantrip, InteractionHand hand);
//     public void delay(Player player, ICantrip cantrip, InteractionHand hand) {
//         if (player.level() instanceof ServerLevel serverLevel)
//             serverLevel.getServer().tell(new TickTask(serverLevel.getServer().getTickCount() + cantrip.getDelay() + 1, () -> run(player, cantrip, hand)));
//     }
// }
