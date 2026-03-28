<script lang="ts">
    import MainButton from "./buttons/MainButton.svelte";
    import ChildButton from "./buttons/ChildButton.svelte";
    import ButtonContainer from "../common/buttons/ButtonContainer.svelte";
    import IconTextButton from "../common/buttons/IconTextButton.svelte";
    import IconButton from "../common/buttons/IconButton.svelte";
    import {
        browsePath,
        exitClient,
        getClientUpdate,
        openScreen,
        toggleBackgroundShaderEnabled
    } from "../../../integration/rest";
    import Menu from "../common/Menu.svelte";
    import {fly} from "svelte/transition";
    import {onMount} from "svelte";
    import {notification} from "../common/header/notification_store";

    let regularButtonsShown = true;
    let clientButtonsShown = false;

    onMount(() => {
        setTimeout(async () => {
            const clientUpdate = await getClientUpdate();

            if (clientUpdate.update) {
                notification.set({
                    title: `LiquidBounce ${clientUpdate.update.clientVersion} has been released!`,
                    message: `Download it from liquidbounce.net!`,
                    error: false,
                    delay: 99999999
                });
            }
        }, 2000);
    });

    function toggleButtons() {
        if (clientButtonsShown) {
            clientButtonsShown = false;
            setTimeout(() => {
                regularButtonsShown = true;
            }, 750);
        } else {
            regularButtonsShown = false;
            setTimeout(() => {
                clientButtonsShown = true;
            }, 750);
        }
    }
</script>

<Menu>
    <div class="content">
        <div class="main-buttons">
            {#if regularButtonsShown}
                <MainButton title="单人游戏" icon="singleplayer" index={0}
                            on:click={() => openScreen("singleplayer")}/>

                <MainButton title="多人游戏" icon="multiplayer" let:parentHovered
                            on:click={() => openScreen("multiplayer")} index={1}>
                    <ChildButton title="Realms" icon="realms" {parentHovered}
                                 on:click={() => openScreen("multiplayer_realms")}/>
                </MainButton>
                <MainButton title="LiquidGalaxy" icon="liquidbounce" on:click={toggleButtons} index={2}/>
                <MainButton title="设置" icon="options" on:click={() => openScreen("options")} index={3}/>
            {:else if clientButtonsShown}
                <MainButton title="代理管理器" icon="proxymanager" on:click={() => openScreen("proxymanager")}
                            index={0}/>
                <MainButton title="功能列表" icon="clickgui" on:click={() => openScreen("clickgui")} index={1}/>
                <!-- <MainButton title="Scripts" icon="scripts" index={2}/> -->
                <MainButton title="返回" icon="back-large" on:click={toggleButtons} index={2}/>
            {/if}
        </div>

        <div class="additional-buttons" transition:fly|global={{duration: 700, y: 100}}>
            <ButtonContainer>
                <IconTextButton icon="icon-exit.svg" title="退出" on:click={exitClient}/>
                <IconTextButton icon="icon-change-background.svg" title="切换着色器"
                                on:click={toggleBackgroundShaderEnabled}/>
            </ButtonContainer>
        </div>

        <div class="social-buttons" transition:fly|global={{duration: 700, y: 100}}>
            <ButtonContainer>
                <IconButton title="GitHub" icon="github" on:click={() => browsePath("https://github.com/g255x/LiquidGalaxy")}/>
            </ButtonContainer>
        </div>
    </div>
</Menu>

<style>
    .content {
        flex: 1;
        display: grid;
        grid-template-areas:
            "a ."
            "b c";
        grid-template-rows: 1fr max-content;
        grid-template-columns: 1fr max-content;
    }

    .main-buttons {
        display: flex;
        flex-direction: column;
        row-gap: 25px;
        grid-area: a;
    }

    .additional-buttons {
        grid-area: b;
    }

    .social-buttons {
        grid-area: c;
    }
</style>
