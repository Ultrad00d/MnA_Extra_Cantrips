package net.ultrad00d.ForgottenCantrips.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogueClientHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClientboundWizardDialoguePacket {
    private final Component messageComponent;
    private final List<ChoiceData> choices;

    public record ChoiceData(String choiceKey, boolean isLocked, String token) {}

    public ClientboundWizardDialoguePacket(Component messageComponent, List<ChoiceData> choices) {
        this.messageComponent = messageComponent;
        this.choices = choices;
    }

    public ClientboundWizardDialoguePacket(FriendlyByteBuf buf) {
        this.messageComponent = buf.readComponent();
        int size = buf.readVarInt();
        this.choices = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.choices.add(new ChoiceData(buf.readUtf(), buf.readBoolean(), buf.readUtf()));
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(this.messageComponent);
        buf.writeVarInt(this.choices.size());
        for (ChoiceData choice : this.choices) {
            buf.writeUtf(choice.choiceKey());
            buf.writeBoolean(choice.isLocked());
            buf.writeUtf(choice.token());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        WizardDialogueClientHandler.renderDialogue(this.messageComponent, this.choices)
                )
        );
        context.setPacketHandled(true);
    }
}